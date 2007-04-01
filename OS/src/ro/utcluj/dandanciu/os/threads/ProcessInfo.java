package ro.utcluj.dandanciu.os.threads;


public class ProcessInfo {
	
	private ThreadInfo threadInfo;
	
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
	 * @return the threadInfo
	 */
	public ThreadInfo getThreadInfo() {
		return threadInfo;
	}

	/**
	 * @param threadInfo the threadInfo to set
	 */
	public void setThreadInfo(ThreadInfo threadInfo) {
		this.threadInfo = threadInfo;
	}
	
	
	
	

}
