package ro.utcluj.dandanciu.nachos.ostomachine;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import ro.utcluj.dandanciu.nachos.common.ConfigOptions;
import ro.utcluj.dandanciu.nachos.common.ProcessorHelper;

public class ThreadContextHelper<T extends Runnable> {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(ThreadContextHelper.class);

	private T target;

	/**
	 * A flag showing if this ThreadContextHelper is running or not.
	 */
	private boolean running = false;

	/**
	 * Flag showing if the current thread is done.
	 */
	private boolean done = false;

	/**
	 * Flag showing if the this ThreadContextHelper has a XThread associated.
	 */
	private boolean associated = false;

	/**
	 * Holds a reference to the java thread responsible for running the current
	 * <code>T</code> thread
	 */
	private Thread javaThread;

	/**
	 * Reference to <code>ProcessorHelper<code> class, 
	 * when this is <code>null<code> this thread is not running
	 */
	private ProcessorHelper processorHelper = null;
	
	/**
	 * We need this because, if this is the first thread we are starting than we
	 * ca steel the current thread
	 */
	private static boolean firstThread = true;


	@SuppressWarnings("unchecked")
	public void start(T target) {
		
		assert (!associated);
		
		this.target = target;
		
		this.associated = true;
		
		this.processorHelper = ProcessorHelper.getAvailableProcessorHelper();
		
		assert (this.processorHelper != null);
		
		this.processorHelper.use((ThreadContextHelper<Runnable>) this);
		
		this.running = true;
		if(firstThread) {
			firstThread = false;
			
			target.run();
		} else {
			javaThread = new Thread(target);
		
			this.javaThread.start();
		}
	}

	private synchronized void waitForInterrupt() {
		logger.info("WAIT FOR INTERRUPT");
		while (!running) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
	}

	private synchronized void interrupt() {
		this.running = true;
		notify();
	}

	@SuppressWarnings("unchecked")
	public void switchContext(ThreadContextHelper<T> previous) {

		assert (this.associated);

		previous.running = false;
		this.processorHelper = previous.processorHelper;
		this.processorHelper.use((ThreadContextHelper<Runnable>) this);
		this.interrupt();
		previous.yield();
	}

	@SuppressWarnings("unchecked")
	public void yield() {
		this.processorHelper.idle();
		this.processorHelper = null;
		this.running = false;
		waitForInterrupt();
		
		if (done) {
			this.interrupt(); //for safety
			throw new ThreadDeath();
		}
		
		this.processorHelper = ProcessorHelper.getAvailableProcessorHelper();
	}

	@SuppressWarnings("unchecked")
	public void resume() {
		this.processorHelper = ProcessorHelper.getAvailableProcessorHelper();
		assert (processorHelper != null);
		this.processorHelper.use((ThreadContextHelper<Runnable>) this);
		interrupt();
	}

	public T getCurrent() {
		return target;
	}
}
