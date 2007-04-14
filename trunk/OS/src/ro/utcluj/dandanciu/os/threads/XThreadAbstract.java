/**
 * 
 */
package ro.utcluj.dandanciu.os.threads;

import java.util.ArrayList;
import java.util.List;

import ro.utcluj.dandanciu.nachos.common.ConfigOptions;
import ro.utcluj.dandanciu.nachos.ostomachine.ThreadContextHelper;

/**
 * @author Dan Danciu
 *
 */
public abstract class XThreadAbstract implements XThread {
	
	protected ThreadInfo info;
	
	protected AddressSpace addressSpace;
	
	protected ThreadContextHelper<XThread> tch = null;
	
	protected String name;

	private List<XThreadAbstract> joinedThreads = new ArrayList<XThreadAbstract>();
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
	}

	public XThreadAbstract(String name) {
		this();
		this.name = name;
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
	public <T extends XThread> void fork(T parent) {
		info.setState(ThreadState.READY);
		
		ProcessManager.enterRegion();
		SystemTask.getInstance().kernellCall(KernelCallType.SYS_FORK, new Object[] {
				this, parent
		});
		
		ProcessManager.addReadyThread(this);
		tch = ThreadContextHelper.getNewThreadContextHelper();
		tch.start(this);
		
		ProcessManager.exitRegion();
	}

	
	/**
	 * This methods should be called when a thread has finished.
	 * <br>
	 * This method also wakes up any thread which has been joined to
	 * this thread.
	 * <br>
	 * Also set the threads state to dead.
	 */
	public void finish() {
		
		for(XThreadAbstract xthread : joinedThreads) {
			xthread.unblock();
		}
		info.setState(ThreadState.DEAD);
	}

	/**
	 * Makes this thread to stay in BLOCKED state until the current 
	 * thread has finished.
	 * Each thread can be joined only to one thread.
	 */
	public <T extends XThread> void join(T current) {
		ProcessManager.enterRegion();
		
		this.blocked();
		XThreadAbstract ct = (XThreadAbstract) current;
		
		ct.joinedThreads.add(this);
		//TODO: ckeck this, should this wait in a loop untill current has finished
		ProcessManager.exitRegion();
	}

	private void blocked() {
		info.setState(ThreadState.BLOCKED);
	}

	private void unblock() {
		info.setState(ThreadState.READY);		
		ProcessManager.addReadyThread(this);
	}

	public void sleep(long ticks) {
		info.setState(ThreadState.BLOCKED);
		ProcessManager.enterRegion();
		//TODO: add the alarm which will wakeup this thread
		SystemTask.getInstance().kernellCall(KernelCallType.SYS_SETALARM,
				new Object[] {new Long(ticks),
						new WakeUpAlarm(this)});
    	XThreadAbstract nextThread = (XThreadAbstract) ProcessManager.next2Run();
	    if (nextThread != null) {
	    	nextThread.switchFrom(this);
	    }
	    ProcessManager.exitRegion();
	}

	/* (non-Javadoc)
	 * @see ro.utcluj.dandanciu.os.threads.XThread#yeld()
	 */
	public void yield() {
	    XThreadAbstract nextThread;


	    ProcessManager.enterRegion();
	    
	    nextThread = (XThreadAbstract ) ProcessManager.next2Run();
	    if (nextThread != null) {
	    	ProcessManager.addReadyThread(this);
	    	nextThread.switchFrom(this);
	    }
	    ProcessManager.exitRegion();
	  }

	private void switchFrom(XThreadAbstract previous) {
		previous.saveContext();
		this.restoreContext();
		this.tch.switchContext(previous.tch);
	}
	/**
	 * Restores the context of the thread.
	 * <br>
	 * Just like save context shouldn't do any work beside 
	 * the threads which are using perisable information holders
	 * <br>
	 * At most this menthods should do logging
	 */
	protected abstract void restoreContext();

	/**
	 * Saves the threads context, in threads which are fully
	 * emulated in java this shouldn't do anything.
	 * <br>
	 * In other threads, like for example those implementing user
	 * programs this method should save any specific atribute
	 * <br>
	 * At most this methods should do logging
	 */
	protected abstract void saveContext();

	/*
	 * Holds what the thread will be doing.
	 */
	public abstract void run();
	
	private final class WakeUpAlarm implements Runnable{
		XThreadAbstract target = null;
		
		public WakeUpAlarm(XThreadAbstract target) {
			this.target = target;
		}
		
		public void run(){
			target.unblock();
		}		
	}
}
