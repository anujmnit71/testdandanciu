package ro.utcluj.dandanciu.os.threads.syncronization;

import org.apache.log4j.Logger;

import java.util.LinkedList;
import java.util.Queue;

import ro.utcluj.dandanciu.os.threads.XThread;
import ro.utcluj.dandanciu.os.threads.servers.ProcessManager;

public class Semaphore {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(Semaphore.class);;

	private String name;

	private int value;

	private Queue<XThread> queue;

	/**
	 * Initialize a semaphore, so that it can be used for synchronization.
	 * "debugName" is an arbitrary name, useful for debugging. "initialValue" is
	 * the initial value of the semaphore.
	 */

	// ----------------------------------------------------------------------
	public Semaphore(String debugName, int initialValue) {
		name = debugName;
		value = initialValue;
		queue = new LinkedList<XThread>();

	}

	/**
	 * Wait until semaphore value > 0, then decrement. Checking the value and
	 * decrementing must be done atomically, so we need to disable interrupts
	 * before checking the value.
	 * 
	 * Note that Thread::Sleep assumes that interrupts are disabled when it is
	 * called.
	 */
	public void P(XThread requester) {
		if (logger.isDebugEnabled()) {
			logger.debug("P::" + name + "(XThread) - start"); 
		}

		ProcessManager.enterRegion();

		while (value == 0) { // semaphore not available
			queue.offer(requester);
			requester.yield(); //TODO: check this (may be ok)
		}
		value--; // semaphore available,
		// consume its value

		ProcessManager.exitRegion();

		if (logger.isDebugEnabled()) {
			logger.debug("P::" + name + "(XThread) - end"); 
		}
	}

	/**
	 * Increment semaphore value, waking up a waiter if necessary. As with P(),
	 * this operation must be atomic, so we need to disable interrupts.
	 * Scheduler.readyToRun() assumes that threads are disabled when it is
	 * called.
	 */
	public void V() {
		if (logger.isDebugEnabled()) {
			logger.debug("V::" + name + "() - start"); 
		}

		XThread thread;
		ProcessManager.enterRegion();

		thread = (XThread) queue.remove();
		if (thread != null) // make thread ready, consuming the V immediately
			ProcessManager.addReadyThread(thread);
		value++;
		ProcessManager.exitRegion();

		if (logger.isDebugEnabled()) {
			logger.debug("V::" + name + "() - end"); 
		}
	}

}
