package ro.utcluj.dandanciu.os.threads.syncronization;

import org.apache.log4j.Logger;

import ro.utcluj.dandanciu.os.threads.XThread;

public class Lock {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(Lock.class);

	private String name; // for debugging

	// plus some other stuff you'll need to define

	private Semaphore sem; // semaphore used for implementation of lock

	private XThread owner; // which thread currently holds this lock?

	public Lock(String debugName) {
		name = debugName;
		sem = new Semaphore("Semaphore for lock \"" + name + "\"", 1);
		owner = null;
	}

	public void acquire(XThread requester) {
		if (logger.isDebugEnabled()) {
			logger.debug("acquire::" + name + "(XThread) - start"); 
		}

		sem.P(requester);
		owner = requester;

		if (logger.isDebugEnabled()) {
			logger.debug("acquire::" + name + "(XThread) - end"); 
		}
	}

	public void release(XThread requester) {
		if (logger.isDebugEnabled()) {
			logger.debug("release::" + name + "(XThread) - start"); 
		}

		assert (requester == owner);
		owner = null;
		sem.V();

		if (logger.isDebugEnabled()) {
			logger.debug("release::" + name + "(XThread) - end"); 
		}
	}

	// a predicate which determines whether or not the lock is held by the
	// current thread. Used for sanity checks in condition variables.
	public boolean isHeldByThread(XThread requester) {
		if (logger.isDebugEnabled()) {
			logger.debug("isHeldByThread::" + name + "(XThread) - start"); 
		}

		boolean returnboolean = (owner.equals(requester));
		if (logger.isDebugEnabled()) {
			logger.debug("isHeldByThread::" + name + "(XThread) - end"); 
		}
		return returnboolean;
	}

}
