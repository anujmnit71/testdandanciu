package ro.utcluj.dandanciu.os.threads;

import java.io.File;
import java.util.ArrayList;

import ro.utcluj.dandanciu.nachos.common.InterruptCode;
import ro.utcluj.dandanciu.nachos.machine.Machine;
import ro.utcluj.dandanciu.nachos.ostomachine.InterruptServiceTable;
import ro.utcluj.dandanciu.nachos.ostomachine.ThreadContextHelper;
import ro.utcluj.dandanciu.os.filesystem.FileSystem;
import ro.utcluj.dandanciu.os.filesystem.stub.StubFileSystem;
import ro.utcluj.dandanciu.os.threads.kernel.GenericKernel;
import ro.utcluj.dandanciu.os.threads.kernel.ThreadsKernel;
import ro.utcluj.dandanciu.os.threads.servers.FileSystemServer;
import ro.utcluj.dandanciu.os.threads.servers.InformationServer;
import ro.utcluj.dandanciu.os.threads.tasks.ClockTask;
import ro.utcluj.dandanciu.os.threads.tasks.SystemTask;
import ro.utcluj.dandanciu.os.threads.util.MutableObject;
import ro.utcluj.dandanciu.os.threads.util.Priority;
import ro.utcluj.dandanciu.os.utils.OsConfigOptions;
import ro.utcluj.dandanciu.os.utils.Utils;

public class Kernel extends XThreadAbstract implements GenericKernel, ThreadsKernel {
	
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
	
	public void terminate() {
		//TODO add termination code
	}

	public void initialize() {
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

	/* (non-Javadoc)
	 * @see ro.utcluj.dandanciu.os.threads.ThreadsKernel#fork(ro.utcluj.dandanciu.os.threads.XThreadAbstract, ro.utcluj.dandanciu.os.threads.XThreadAbstract)
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
		//TODO add message to user
		Machine.halt();
	}
	/* (non-Javadoc)
	 * @see ro.utcluj.dandanciu.os.threads.ThreadsKernel#getThreadInfo(int)
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

	/* (non-Javadoc)
	 * @see ro.utcluj.dandanciu.os.threads.ThreadsKernel#exec(ro.utcluj.dandanciu.os.threads.XThreadAbstract, java.lang.String, java.lang.Integer, java.lang.String)
	 */
	public int exec(XThreadAbstract theThread, String executable, Integer argc, String argv) {
		//TODO generate new process info
		//TODO generate new thread info
		return -1;
	}
	
	/* (non-Javadoc)
	 * @see ro.utcluj.dandanciu.os.threads.ThreadsKernel#exit(ro.utcluj.dandanciu.os.threads.XThreadAbstract, java.lang.Integer)
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

	/* (non-Javadoc)
	 * @see ro.utcluj.dandanciu.os.threads.ThreadsKernel#join(ro.utcluj.dandanciu.os.threads.XThreadAbstract, java.lang.Integer, ro.utcluj.dandanciu.os.threads.util.MutableObject)
	 */
	public Object join(XThreadAbstract theThread, Integer processId, MutableObject exitCode) {
		// TODO Auto-generated method stub
		return null;
	}
}
