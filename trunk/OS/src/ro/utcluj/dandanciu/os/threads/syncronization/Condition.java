package ro.utcluj.dandanciu.os.threads.syncronization;

import org.apache.log4j.Logger;

import java.util.LinkedList;
import java.util.Queue;

import ro.utcluj.dandanciu.os.threads.ProcessManager;
import ro.utcluj.dandanciu.os.threads.XThread;

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
		ProcessManager.enterRegion();
		waitingThreads.offer(requester);
		requester.yield(); //TODO: check this (may be ok)
		ProcessManager.exitRegion();
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
		ProcessManager.enterRegion();
		XThread newThread = (XThread) (waitingThreads.remove());
		if (newThread != null) {
			ProcessManager.addReadyThread(newThread);
		}
		ProcessManager.exitRegion();

		if (logger.isDebugEnabled()) {
			logger.debug("signal::" + name + "(Lock, XThread) - end"); 
		}
	}

	public void broadcast(Lock conditionLock, XThread requester) {
		if (logger.isDebugEnabled()) {
			logger.debug("broadcast::" + name + "(Lock, XThread) - start"); 
		}

	    assert (conditionLock.isHeldByThread(requester));

	    ProcessManager.enterRegion();

	    while (!waitingThreads.isEmpty()) {
	      ProcessManager.addReadyThread((XThread)(waitingThreads.poll()));
	    }

	    ProcessManager.exitRegion();

		if (logger.isDebugEnabled()) {
			logger.debug("broadcast::" + name + "(Lock, XThread) - end"); 
		}
	  }
}
