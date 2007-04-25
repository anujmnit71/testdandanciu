package ro.utcluj.dandanciu.os.threads;

import java.util.ArrayList;


public class ProcessInfo {
	
	private ArrayList<ThreadInfo> threadInfos;
	
	private ProcessManagementInfo processManagementInfo;
	
	private FileManagementInfo fileManagementInfo;
	
	private ArrayList<ProcessInfo> childsInfo;
	
	private ArrayList<ThreadInfo> joinedThreads;
	
	private ProcessInfo parentInfo;

	public ArrayList<ProcessInfo> getChildsInfo() {
		return childsInfo;
	}

	public void setChildsInfo(ArrayList<ProcessInfo> childsInfo) {
		this.childsInfo = childsInfo;
	}

	public ArrayList<ThreadInfo> getJoinedThreads() {
		return joinedThreads;
	}

	public void setJoinedThreads(ArrayList<ThreadInfo> joinedThreads) {
		this.joinedThreads = joinedThreads;
	}

	public ProcessInfo getParentInfo() {
		return parentInfo;
	}

	public void setParentInfo(ProcessInfo parentInfo) {
		this.parentInfo = parentInfo;
	}

	/**
	 * @return the fileManagementInfo
	 */
	public FileManagementInfo getFileManagementInfo() {
		return fileManagementInfo;
	}

	/**
	 * @param fileManagementInfo the fileManagementInfo to set
	 */
	public void setFileManagementInfo(FileManagementInfo fileManagementInfo) {
		this.fileManagementInfo = fileManagementInfo;
	}

	/**
	 * @return the processManagementInfo
	 */
	public ProcessManagementInfo getProcessManagementInfo() {
		return processManagementInfo;
	}

	/**
	 * @param processManagementInfo the processManagementInfo to set
	 */
	public void setProcessManagementInfo(ProcessManagementInfo processManagementInfo) {
		this.processManagementInfo = processManagementInfo;
	}

	/**
	 * Return the thread info with a certain id.
	 * @param id the thread id
	 * @return the threadInfo with the specified id, if no such thread exists 
	 * in this proccess then we return null
	 */
	public ThreadInfo getThreadInfoByThreadId(int id) {
		for(ThreadInfo info : threadInfos) {
			if(info.getThreadId() == id)
				return info;
		}
		return null;
	}

	/**
	 * @param threadInfo the threadInfo to set
	 */
	public void addThreadInfo(ThreadInfo threadInfo) {
		this.threadInfos.add(threadInfo);
	}

	public ProcessInfo() {
		threadInfos = new ArrayList<ThreadInfo>();
		processManagementInfo = new ProcessManagementInfo();
		fileManagementInfo = new FileManagementInfo();
	}

	public ArrayList<ThreadInfo> getThreadInfos() {
		return threadInfos;
	}

	public void setThreadInfos(ArrayList<ThreadInfo> threadInfos) {
		this.threadInfos = threadInfos;
	}
}
