package ro.utcluj.dandanciu.nachos.ostomachine;

import java.util.ArrayList;
import java.util.List;

import ro.utcluj.dandanciu.nachos.common.ConfigOptions;

public class ThreadContextHelper<T extends Runnable> {
	
	private T target;
	/**
	 * The id of this ThreadContextHelper
	 */
	private final int id;
	
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
	 * Holds a reference to the java thread responsible for running the current <code>T</code> thread
	 */
	private Thread javaThread;
	
	/**
	 * Holds a reference to all the ThreadContextHelpers currently running in the system.
	 */
	private static List<ThreadContextHelper<Runnable>> tchs = new ArrayList<ThreadContextHelper<Runnable>>();
	
	static {
		for(int i = 0; i < ConfigOptions.NoOfProcs; i++) {
			tchs.add(new ThreadContextHelper<Runnable>(i));
		}
	}
	
	private ThreadContextHelper(int i) {
		this.id = i;
	}

	public void start(T target) {
		
		assert (!associated);
		
		this.target = target;
		
		this.associated = true;
		
		javaThread = new Thread(target);
		
		this.running = false;
		
		this.javaThread.start();
		
		this.waitForInterrupt();
	}
	
	private synchronized void waitForInterrupt() {
		while (!running) {
		    try { wait(); }
		    catch (InterruptedException e) { }
		}
	}
	
	private synchronized void interrupt() {
		running = true;
		notify();
	}

	
	public void switchContext(ThreadContextHelper<T> previous) {
		
		assert (this.associated);
		
		previous.running = false;
		
		this.interrupt();
		previous.yield();		
	}
	
	@SuppressWarnings("unchecked")
	private void yield() {
		waitForInterrupt();
		
		if (done) {
		    tchs.get(id).interrupt();
		    throw new ThreadDeath();
		}

		tchs.add(id, (ThreadContextHelper<Runnable>) this);
	}
	
	public T getCurrent() {
		return target;
	}
	
	private static int nextToDeliver = 0;	
	public static <T extends Runnable> ThreadContextHelper<T>  getNewThreadContextHelper(){
		ThreadContextHelper<T> current = new ThreadContextHelper<T>(nextToDeliver);
		nextToDeliver = (nextToDeliver + 1) % tchs.size();
		return current;
	}
}
