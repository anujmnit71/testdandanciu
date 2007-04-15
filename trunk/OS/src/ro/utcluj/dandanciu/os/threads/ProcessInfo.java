package ro.utcluj.dandanciu.os.threads;

import java.util.ArrayList;


public class ProcessInfo {
	
	private ArrayList<ThreadInfo> threadInfos;
	
	private ProcessManagementInfo processManagementInfo;
	
	private FileManagementInfo fileManagementInfo;

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
	public ThreadInfo getThreadInfobyThreadId(int id) {
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
}
