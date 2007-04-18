package ro.utcluj.dandanciu.os.threads;

import java.util.ArrayList;
import java.util.List;

import ro.utcluj.dandanciu.nachos.common.InterruptCode;
import ro.utcluj.dandanciu.nachos.ostomachine.InterruptServiceTable;
import ro.utcluj.dandanciu.nachos.ostomachine.ThreadContextHelper;
import ro.utcluj.dandanciu.os.threads.servers.InformationServer;
import ro.utcluj.dandanciu.os.threads.tasks.ClockTask;
import ro.utcluj.dandanciu.os.threads.tasks.SystemTask;
import ro.utcluj.dandanciu.os.threads.util.Priority;

public class Kernel extends XThreadAbstract {
	
	private static final class CONFIG {
		public static final int PROCESS_TABLE_INITIAL_SIZE = 10;
		public static final int THREAD_TABLE_INITIAL_SIZE = 15;
		public static final int PROCESS_TABLE_CAPACITY_INCREMENT = 3;
		public static final int THREAD_TABLE_CAPACITY_INCREMENT = 5;
		
	}

	private static Kernel kernel;

	private ArrayList<ProcessInfo> processTable;
	
	private ArrayList<ThreadInfo> threadTable;

	// Tasks
	private ClockTask clockTask;

	private SystemTask systemTask;

	// Drivers

	// Servers
	private InformationServer informationServer;

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
		threadTable = new ArrayList<ThreadInfo>(CONFIG.THREAD_TABLE_INITIAL_SIZE);		
		initializeKerelInfo();
		
		// TASKS
		systemTask = new SystemTask();
		clockTask = new ClockTask();
		initilizeIsrTable();

		// DRIVERS

		// SERVERS
		informationServer = InformationServer.getInformationServer(
				System.currentTimeMillis());			//TODO: replace this call with one to Machine Emulation
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
		threadTable.add(threadId, tInfo);
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
		ThreadInfo pInfo = threadTable.get(parent.info.getThreadId());
		ThreadInfo cInfo = new ThreadInfo(nextThreadId());
		
		cInfo.setProcessId(pInfo.getProcessId());
		cInfo.setPriority(pInfo.getPriority());
		cInfo.setState(ThreadState.CREATED);

		child.info = cInfo;
		
		this.processTable.get(pInfo.getProcessId()).addThreadInfo(cInfo);
		this.threadTable.add(cInfo);
	}
	
	private int nextProcessId() {
		return nextId(processTable, CONFIG.PROCESS_TABLE_CAPACITY_INCREMENT);
	}
	
	private int nextThreadId() {
		return nextId(threadTable, CONFIG.THREAD_TABLE_CAPACITY_INCREMENT);
	}
	
	private static int nextId(ArrayList which, int capacityIncrement) {
		int i;
		for(i = 0; i < which.size(); i++) {
			if(which.get(i) == null) {
				return i;
			}
		}
		//no free spots in the table, need to increase size
		which.ensureCapacity(capacityIncrement + i - 1);
		return i;
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

	public ThreadInfo getThreadInfo(int threadId) {
		return threadTable.get(threadId);
	}
}
