package ro.utcluj.dandanciu.os.threads.syncronization;

import org.apache.log4j.Logger;

import java.util.LinkedList;
import java.util.Queue;

import ro.utcluj.dandanciu.os.threads.XThread;
import ro.utcluj.dandanciu.os.threads.servers.ThreadManager;

public class Condition {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(Condition.class);

	private String name;

	private Queue<XThread> waitingThreads; // who's waiting on this condition?

	public Condition(String debugName) {
		name = debugName;
		waitingThreads = new LinkedList<XThread>();
	}

	public void wait(Lock conditionLock, XThread requester) {
		if (logger.isDebugEnabled()) {
			logger.debug("wait::" + name + "(Lock, XThread) - start"); 
		}

		assert (conditionLock.isHeldByThread(requester));
		conditionLock.release(requester);
		ThreadManager.enterRegion();
		waitingThreads.offer(requester);
		requester.yield(); //TODO: check this (may be ok)
		ThreadManager.exitRegion();
		conditionLock.acquire(requester);

		if (logger.isDebugEnabled()) {
			logger.debug("wait::" + name + "(Lock, XThread) - end"); 
		}
	}

	public void signal(Lock conditionLock, XThread requester) {
		if (logger.isDebugEnabled()) {
			logger.debug("signal::" + name + "(Lock, XThread) - start"); 
		}

		assert (conditionLock.isHeldByThread(requester));
		ThreadManager.enterRegion();
		XThread newThread = (XThread) (waitingThreads.remove());
		if (newThread != null) {
			ThreadManager.addReadyThread(newThread);
		}
		ThreadManager.exitRegion();

		if (logger.isDebugEnabled()) {
			logger.debug("signal::" + name + "(Lock, XThread) - end"); 
		}
	}

	public void broadcast(Lock conditionLock, XThread requester) {
		if (logger.isDebugEnabled()) {
			logger.debug("broadcast::" + name + "(Lock, XThread) - start"); 
		}

	    assert (conditionLock.isHeldByThread(requester));

	    ThreadManager.enterRegion();

	    while (!waitingThreads.isEmpty()) {
	      ThreadManager.addReadyThread((XThread)(waitingThreads.poll()));
	    }

	    ThreadManager.exitRegion();

		if (logger.isDebugEnabled()) {
			logger.debug("broadcast::" + name + "(Lock, XThread) - end"); 
		}
	  }
}
