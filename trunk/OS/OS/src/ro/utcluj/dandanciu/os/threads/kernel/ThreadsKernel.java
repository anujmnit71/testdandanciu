package ro.utcluj.dandanciu.os.threads.kernel;

import ro.utcluj.dandanciu.os.threads.ThreadInfo;
import ro.utcluj.dandanciu.os.threads.XThreadAbstract;
import ro.utcluj.dandanciu.os.threads.util.MutableObject;

public interface ThreadsKernel {

	/**
	 * Same process new thread
	 * @param child
	 * @param parent
	 */
	void fork(XThreadAbstract child, XThreadAbstract parent);

	/**
	 *	//TODO add javadoc
	 * @param threadId
	 * @return
	 */
	ThreadInfo getThreadInfo(int threadId);

	/**
	 * Creates a new process which will execute the executable with the parameters.
	 * 
	 * What has to be done:
	 * 	//TODO: (1) create a new process, child for the current threads process	
	 * 	//TODO: (2) move the current thread to the new process
	 * 	//TODO: (3) parse the parameters
	 * 	//TODO: (4) make a new ThreadContextHelper which will run the new executable
	 * 	//TODO: (5) return the process Id
	 * 
	 * @param thread
	 * @param executable
	 * @return returns the new process id, if error occured -1 is returned
	 */
	int exec(XThreadAbstract theThread, String executable, Integer argc,
			String argv);

	/**
	 * A process has exited, by calling <code> exit EXIT_CODE</code>, from 
	 * one of its threads, where EXIT_CODE is a integer number.
	 * 
	 * What has to be done:
	 * 	(1) set exit code in process info
	 * 	(2) kill all threads in process
	 * 	(3) notify all processes currently waiting for this process to exit 
	 *  (4) free thread table
	 *  (5) free process table
	 *  (6) set all child process parents to null
	 *  (7) close all opened files
	 * 
	 * @param thread the thread from which the call was triggered
	 * @param exitCode the exit code number, exit code <code>0</code> means that the process
	 * has exited normally 
	 * 
	 */
	void exit(XThreadAbstract theThread, Integer exitCode);

	/**
	 * Suspend execution of the current process until the child process specified
	 * by the processID argument has exited. If the child has already exited by the
	 * time of the call, returns immediately. When the current process resumes, it
	 * disowns the child process, so that join() cannot be used on that process
	 * again.
	 * 
	 * What has to be done: 
	 * 	//TODO: (1) stop current thread (OR current process)
	 * 	//TODO: (2) add thread (OR process) to the waiting list of the process
	 * 
	 * @param theThread the thread invoking the call
	 * @param processId the process id we want to wait for
	 * @param exitCode the exit code
	 * @return If the child exited normally, returns 1. If the child exited as a result of
	 * an unhandled exception, returns 0. If processID does not refer to a child
	 * process of the current process, returns -1.
	 */
	Object join(XThreadAbstract theThread, Integer processId,
			MutableObject exitCode);

}