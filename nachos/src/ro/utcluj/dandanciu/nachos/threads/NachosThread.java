package ro.utcluj.dandanciu.nachos.threads;

import ro.utcluj.dandanciu.nachos.machine.Interrupt;
import ro.utcluj.dandanciu.nachos.machine.Machine;
import ro.utcluj.dandanciu.nachos.userprog.AddrSpace;
import ro.utcluj.dandanciu.nachos.utils.Printable;

// NachosThread.java
//	Nachos threads class.  There are four main methods:
//
//	Fork -- create a thread to run a procedure concurrently
//		with the caller (this is done in two steps -- first
//		allocate the Thread object, then call Fork on it)
//	Finish -- called when the forked procedure finishes, to clean up
//	Yield -- relinquish control over the CPU to another ready thread
//	Sleep -- relinquish control over the CPU, but thread is now blocked.
//		In other words, it will not run again, until explicitly 
//		put back on the ready queue.
//
// Copyright (c) 1992-1993 The Regents of the University of California.
// Copyright (c) 1998 Rice University.
// All rights reserved.  See the COPYRIGHT file for copyright notice and
// limitation of liability and disclaimer of warranty provisions.


public class NachosThread extends Thread implements Printable {
  // Thread state
  public static final int JUST_CREATED = 0;
  public static final int RUNNING = 1;
  public static final int READY = 2;
  public static final int BLOCKED = 3;
  
  // instancs vars
  private Runnable runnableObject;
  private AddrSpace space;
  private int status;		// ready, running or blocked

  // A thread running a user program actually has *two* sets of 
  // CPU registers -- 
  // one for its state while executing user code, one for its state 
  // while executing kernel code.

  private int userRegisters[];	// user-level CPU register state


  public static NachosThread thisThread() {
    return (NachosThread) currentThread();
  }

  public void setStatus(int st) { 
    status = st; 
  }

  public int getStatus() { 
    return status;
  }

  //----------------------------------------------------------------------
  // 	Create a Nachos thread object call Fork() on it.
  //	"threadName" is an arbitrary string, useful for debugging.
  //----------------------------------------------------------------------

  public NachosThread(String threadName)  {
    super(threadName);
    status = JUST_CREATED;
    // user-level CPU register state    
    userRegisters = new int[Machine.NumTotalRegs];
  }

  //----------------------------------------------------------------------
  // finalize()
  // 	called when the thread is garbage collected
  //----------------------------------------------------------------------

  protected void finalize() {
    Debug.print('t', "Deleting thread: " + getName() + "\n");
  }

  public void run() {
    synchronized (this) {
      while (status != RUNNING) {
	// wait until first scheduled
	try {this.wait();} catch (InterruptedException e) {};
      }
    }
    runnableObject.run();
    finish();
  }
  
  //----------------------------------------------------------------------
  // fork
  // 	Make the thread execute runObj.run()
  //----------------------------------------------------------------------

  public void fork(Runnable runObj) {
    Debug.print('t', "Forking thread: " + getName() + "\n");
    Debug.ASSERT((status == JUST_CREATED), 
		 "Attempt to fork a thread that's already been forked");
    runnableObject = runObj;
    start();

    int oldLevel = Interrupt.setLevel(Interrupt.IntOff);
    Scheduler.readyToRun(this);	// ReadyToRun assumes that interrupts 
								// are disabled!
    Interrupt.setLevel(oldLevel);
    
  }    


  public void setSpace(AddrSpace s) {
    space = s;
  }

  //----------------------------------------------------------------------
  // finish
  // 	Called when a thread is done executing the 
  //	runnableObject
  //----------------------------------------------------------------------

  public void finish() {
    Interrupt.setLevel(Interrupt.IntOff);		
    Debug.ASSERT(this == NachosThread.thisThread());

    Debug.print('t', "Finishing thread: " + getName() +"\n");

    Scheduler.threadToBeDestroyed = thisThread();
    sleep();				
    // not reached
  }


  //----------------------------------------------------------------------
  // Yield
  // 	Relinquish the CPU if any other thread is ready to run.
  //	If so, put the thread on the end of the ready list, so that
  //	it will eventually be re-scheduled.
  //
  //	NOTE: returns immediately if no other thread on the ready queue.
  //	Otherwise returns when the thread eventually works its way
  //	to the front of the ready list and gets re-scheduled.
  //
  //	NOTE: we disable interrupts, so that looking at the thread
  //	on the front of the ready list, and switching to it, can be done
  //	atomically.  On return, we re-set the interrupt level to its
  //	original state, in case we are called with interrupts disabled. 
  //
  // 	Similar to sleep(), but a little different.
  //----------------------------------------------------------------------

  public void Yield () {
    NachosThread nextThread;
    int oldLevel = Interrupt.setLevel(Interrupt.IntOff);
    
    Debug.ASSERT(this == NachosThread.thisThread());
    
    Debug.println('t', "Yielding thread: " + getName());
    
    nextThread = Scheduler.findNextToRun();
    if (nextThread != null) {
    	Scheduler.readyToRun(this);
    	Scheduler.run(nextThread);
    }
    Interrupt.setLevel(oldLevel);
  }



  //----------------------------------------------------------------------
  // sleep
  // 	Relinquish the CPU, because the current thread is blocked
  //   waiting on a synchronization variable (Semaphore, Lock, or Condition).
  //	Eventually, some thread will wake this thread up, and put it
  //	back on the ready queue, so that it can be re-scheduled.
  //
  //	NOTE: if there are no threads on the ready queue, that means
  //	we have no thread to run.  "Interrupt::Idle" is called
  //	to signify that we should idle the CPU until the next I/O interrupt
  //	occurs (the only thing that could cause a thread to become
  //	ready to run).
  //
  //	NOTE: we assume interrupts are already disabled, because it
  //	is called from the synchronization routines which must
  //	disable interrupts for atomicity.   We need interrupts off 
  //	so that there can't be a time slice between pulling the first thread
  //	off the ready list, and switching to it.
  //----------------------------------------------------------------------
  public void sleep () {
    NachosThread nextThread;
    
    Debug.ASSERT(this == NachosThread.thisThread());
    Debug.ASSERT(Interrupt.getLevel() == Interrupt.IntOff);
    
    Debug.println('t', "Sleeping thread: " + getName());

    status = BLOCKED;
    while ((nextThread = Scheduler.findNextToRun()) == null)
      Interrupt.idle();	// no one to run, wait for an interrupt
        
    Scheduler.run(nextThread); // returns when we've been signalled
  }


  //----------------------------------------------------------------------
  // saveUserState
  //	Save the CPU state of a user program on a context switch.
  //
  //	Note that a user program thread has *two* sets of CPU registers -- 
  //	one for its state while executing user code, one for its state 
  //	while executing kernel code.  This routine saves the former.
  //----------------------------------------------------------------------

  public void saveUserState() {
    for (int i = 0; i < Machine.NumTotalRegs; i++)
      userRegisters[i] = Machine.readRegister(i);
  }

  //----------------------------------------------------------------------
  // restoreUserState
  //	Restore the CPU state of a user program on a context switch.
  //
  //	Note that a user program thread has *two* sets of CPU registers -- 
  //	one for its state while executing user code, one for its state 
  //	while executing kernel code.  This routine restores the former.
  //----------------------------------------------------------------------

  public void restoreUserState() {
    for (int i = 0; i < Machine.NumTotalRegs; i++)
      Machine.writeRegister(i, userRegisters[i]);
  }

  // print
  public void print() {
    System.out.print(getName() + ", ");
  }

public AddrSpace getSpace() {
	return space;
}

}

