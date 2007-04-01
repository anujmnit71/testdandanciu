/**
 * 
 */
package ro.utcluj.dandanciu.os.threads;

import ro.utcluj.dandanciu.nachos.common.ConfigOptions;

/**
 * @author Dan Danciu
 *
 */
public abstract class XThreadAbstract implements XThread {
	
	private ThreadInfo info;
	
	private AddressSpace addressSpace;
	
	private Runnable runnableObject = null;
	/**
	 * Create a new XThread in the OS.
	 * It creates the address space, initializes registers to zero
	 * and sets the the state to created.
	 * 
	 * The only valid call on this object at this moment is for.
	 *
	 */
	public XThreadAbstract(){
		info = new ThreadInfo("Unknown");
		info.setRegisters(new int[ConfigOptions.NoOfRegisters]);
		info.setState(ThreadState.CREATED);
		
		addressSpace = new AddressSpace();
		
		this.runnableObject = null;
	}

	/**
	 * Starts the new XThread. 
	 * Requires a Runnable object, this objects run method will be called
	 * the running task of this process.
	 * 
	 * Afther this call this thread state will be READY, which means will be
	 * added to the ready threads queue of the sheduler.
	 *  
	 *@param runnable The object which tells what this thread will be doing
	 *
	 */
	public void fork(Runnable runnable) {
		this.runnableObject = runnable;
		
		info.setState(ThreadState.READY);
		
		//TODO: handle it by the proccess managers

	}

	
	/* (non-Javadoc)
	 * @see ro.utcluj.dandanciu.os.threads.XThread#finish()
	 */
	public void finish() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see ro.utcluj.dandanciu.os.threads.XThread#join()
	 */
	public void join() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see ro.utcluj.dandanciu.os.threads.XThread#sleep()
	 */
	public void sleep() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see ro.utcluj.dandanciu.os.threads.XThread#yeld()
	 */
	public void yeld() {
		// TODO Auto-generated method stub

	}

	/*
	 * Holds what the thread will be doing.
	 */
	public abstract void run();
}
