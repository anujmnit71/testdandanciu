package ro.utcluj.dandanciu.os.threads;

import java.io.File;
import java.util.ArrayList;

import ro.utcluj.dandanciu.nachos.common.InterruptCode;
import ro.utcluj.dandanciu.nachos.ostomachine.InterruptServiceTable;
import ro.utcluj.dandanciu.nachos.ostomachine.ThreadContextHelper;
import ro.utcluj.dandanciu.os.filesystem.FileSystem;
import ro.utcluj.dandanciu.os.filesystem.stub.StubFileSystem;
import ro.utcluj.dandanciu.os.threads.servers.FileSystemServer;
import ro.utcluj.dandanciu.os.threads.servers.InformationServer;
import ro.utcluj.dandanciu.os.threads.tasks.ClockTask;
import ro.utcluj.dandanciu.os.threads.tasks.SystemTask;
import ro.utcluj.dandanciu.os.threads.util.MutableObject;
import ro.utcluj.dandanciu.os.threads.util.Priority;
import ro.utcluj.dandanciu.os.utils.OsConfigOptions;
import ro.utcluj.dandanciu.os.utils.Utils;

public class Kernel extends XThreadAbstract {
	
	private static final class CONFIG {
		public static final int PROCESS_TABLE_INITIAL_SIZE = 10;
		public static final int THREAD_TABLE_INITIAL_SIZE = 15;
		public static final int PROCESS_TABLE_CAPACITY_INCREMENT = 3;
		public static final int THREAD_TABLE_CAPACITY_INCREMENT = 5;
		
	}

	private static Kernel kernel;

	private ArrayList<ProcessInfo> processTable;
	
	private ArrayList<XThreadAbstract> threadTable;

	// Tasks
	private ClockTask clockTask;

	private SystemTask systemTask;

	// Drivers

	// Servers
	private InformationServer informationServer;
	//most servers work statically 

	/**
	 * @return the systemTask
	 */
	public SystemTask getSystemTask() {
		return systemTask;
	}

	/**
	 * @param systemTask
	 *            the systemTask to set
	 */
	public void setSystemTask(SystemTask systemTask) {
		this.systemTask = systemTask;
	}

	public static Kernel getKernel() {
		if(kernel == null) {
			kernel = new Kernel();
			kernel.tch = new ThreadContextHelper<XThread>();
			kernel.tch.start(kernel);
		}
		return kernel;
	}

	private void initialize() {
		// initialize the process information table
		processTable = new ArrayList<ProcessInfo>(CONFIG.PROCESS_TABLE_INITIAL_SIZE);
		threadTable = new ArrayList<XThreadAbstract>(CONFIG.THREAD_TABLE_INITIAL_SIZE);		
		initializeKerelInfo();
		
		initializeFileSystem();
		
		// TASKS
		systemTask = new SystemTask();
		clockTask = new ClockTask();
		initilizeIsrTable();

		// DRIVERS

		// SERVERS
		informationServer = InformationServer.getInformationServer(
				System.currentTimeMillis());			//TODO: replace this call with one to Machine Emulation
	}

	private void initializeFileSystem() {
		FileSystem fs = new StubFileSystem(new File(OsConfigOptions.StubFileSystemRoot));
		FileSystemServer.setFileSystem(fs);
	}

	private void initializeKerelInfo() {
		ProcessInfo kInfo = new ProcessInfo();
		
		int processId = nextProcessId();
		
		kInfo.getProcessManagementInfo().setProcessId(processId);
		kInfo.getProcessManagementInfo().setParentId(-1);	//we don't have a parent process
		kInfo.getProcessManagementInfo().setGroupId(processId);
		kInfo.getProcessManagementInfo().setUserId(-1);
		kInfo.getProcessManagementInfo().setUserGroupId(-1);
		kInfo.getProcessManagementInfo().setEffectiveUserId(-1);
		kInfo.getProcessManagementInfo().setEffectiveUserGroupId(-1);

		int threadId = nextThreadId();
		ThreadInfo tInfo = new ThreadInfo(threadId);
		kInfo.addThreadInfo(tInfo);
		tInfo.setProcessId(kInfo.getProcessManagementInfo().getProcessId());
		tInfo.setName("KERNEL v0.1 ALPHA");
		tInfo.setPriority(Priority.MAX_PRIORITY);
		tInfo.setEfectivePriority(Priority.MAX_PRIORITY);
		tInfo.setState(ThreadState.CREATED);

		//TODO: add information for file management
		
		this.info = tInfo;
		threadTable.add(threadId, this);
		processTable.add(processId, kInfo);
		
	}
	
	private void initilizeIsrTable(){
		//TODO add all other service routines 
		InterruptServiceTable.setInterruptService(InterruptCode.TICK, clockTask);
	}

	@Override
	public void run() {
		initialize();
		
	}

	/**
	 * Same process new thread
	 * @param child
	 * @param parent
	 */
	public void fork(XThreadAbstract child, XThreadAbstract parent) {
		ThreadInfo pInfo = threadTable.get(parent.info.getThreadId()).info;
		ThreadInfo cInfo = new ThreadInfo(nextThreadId());
		
		cInfo.setProcessId(pInfo.getProcessId());
		cInfo.setPriority(pInfo.getPriority());
		cInfo.setState(ThreadState.CREATED);

		child.info = cInfo;
		
		this.processTable.get(pInfo.getProcessId()).addThreadInfo(cInfo);
		this.threadTable.add(child);
	}
	
	private int nextProcessId() {
		return Utils.nextId(processTable, CONFIG.PROCESS_TABLE_CAPACITY_INCREMENT);
	}
	
	private int nextThreadId() {
		return Utils.nextId(threadTable, CONFIG.THREAD_TABLE_CAPACITY_INCREMENT);
	}
	
	@Override
	protected void restoreContext() {
	}

	@Override
	protected void saveContext() {
	}

	/**
	 * @return the clockTask
	 */
	public ClockTask getClockTask() {
		return clockTask;
	}

	/**
	 * @param clockTask
	 *            the clockTask to set
	 */
	public void setClockTask(ClockTask clockTask) {
		this.clockTask = clockTask;
	}

	public void panic() {
		assert false;
		// TODO implement Kernel.panic
	}
	/**
	 *	//TODO add javadoc
	 * @param threadId
	 * @return
	 */
	public ThreadInfo getThreadInfo(int threadId) {
		return threadTable.get(threadId).info;
	}
	
	/**
	 *  //TODO add javadoc
	 * @param threadId
	 * @return
	 */
	public FileManagementInfo getFileManagementInfo(int threadId) {
		return processTable.get(threadTable.get(threadId).info.getProcessId()).getFileManagementInfo();
	}

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
	public int exec(XThreadAbstract theThread, String executable, Integer argc, String argv) {
		//TODO generate new process info
		//TODO generate new thread info
		return -1;
	}
	
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
	public void exit(XThreadAbstract theThread, Integer exitCode) {
		ProcessInfo processInfo = processTable.get(theThread.info.getProcessId());
		processInfo.getProcessManagementInfo().setExitStatus(exitCode); //(1)
		for(ThreadInfo threadInfo : processInfo.getThreadInfos()) {
			XThreadAbstract thread = threadTable.get(threadInfo.getThreadId());
			threadTable.set(thread.getThreadId(), null); //(4)
			thread.finish(); //(2) & (3)
		}
		for(ProcessInfo info : processInfo.getChildsInfo()) {
			info.setParentInfo(null);	//(6)
		}
		for(ThreadInfo info : processInfo.getJoinedThreads()) {
			XThreadAbstract thread = threadTable.get(info.getThreadId());//(3)
			//TODO set the exit code: WHERE????
		}
		processTable.set(processInfo.getProcessManagementInfo().getProcessId(), null); //(5)
		//TODO: close opened files (7)
	}

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
	public Object join(XThreadAbstract theThread, Integer processId, MutableObject exitCode) {
		// TODO Auto-generated method stub
		return null;
	}
}
