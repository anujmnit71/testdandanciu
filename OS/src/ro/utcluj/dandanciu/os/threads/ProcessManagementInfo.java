package ro.utcluj.dandanciu.os.threads;

public class ProcessManagementInfo {
	/**
	 * Pointer to text segment
	 */
	
	/**
	 * Pointer to data segment 
	 */
	
	/** 
	 * Pointer to bss segment
	 */
	
	/**
	 * Exit status
	 */
	
	/**
	 * Signal status
	 */
	
	/**
	 * Process ID (PID)
	 */
	private int processId;
	
	/**
	 * Parent process PID
	 */
	private int parentId;
	
	/**
	 * Process group (GID)
	 */
	private int groupId;
	
	/**
	 * Children's CPU time
	 */
	private int childrensTicks = 0;
	
	/**
	 * Real UID
	 */
	private int userId;
	
	/**
	 * Effective UID
	 */
	private int effectiveUserId;
	
	/**
	 * Real GID
	 */
	private int userGroupId;
	
	/**
	 * Effective GID
	 */
	private int effectiveUserGroupId;
	
	/**
	 * Thread efective priority, this is used for scheduling
	 * When there are threads blocked after this thread if their 
	 * priority is greater than its own, the thread borrows the priotity
	 */
	private int efectivePriority = 0;

	/**
	 * @return the childrensTicks
	 */
	public int getChildrensTicks() {
		return childrensTicks;
	}

	/**
	 * @param childrensTicks the childrensTicks to set
	 */
	public void setChildrensTicks(int childrensTicks) {
		this.childrensTicks = childrensTicks;
	}

	/**
	 * @return the efectivePriority
	 */
	public int getEfectivePriority() {
		return efectivePriority;
	}

	/**
	 * @param efectivePriority the efectivePriority to set
	 */
	public void setEfectivePriority(int efectivePriority) {
		this.efectivePriority = efectivePriority;
	}

	/**
	 * @return the effectiveUserGroupId
	 */
	public int getEffectiveUserGroupId() {
		return effectiveUserGroupId;
	}

	/**
	 * @param effectiveUserGroupId the effectiveUserGroupId to set
	 */
	public void setEffectiveUserGroupId(int effectiveUserGroupId) {
		this.effectiveUserGroupId = effectiveUserGroupId;
	}

	/**
	 * @return the efFectiveUserId
	 */
	public int getEffectiveUserId() {
		return effectiveUserId;
	}

	/**
	 * @param efFectiveUserId the efFectiveUserId to set
	 */
	public void setEffectiveUserId(int efFectiveUserId) {
		this.effectiveUserId = efFectiveUserId;
	}

	/**
	 * @return the groupId
	 */
	public int getGroupId() {
		return groupId;
	}

	/**
	 * @param groupId the groupId to set
	 */
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	/**
	 * @return the parentId
	 */
	public int getParentId() {
		return parentId;
	}

	/**
	 * @param parentId the parentId to set
	 */
	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	/**
	 * @return the processId
	 */
	public int getProcessId() {
		return processId;
	}

	/**
	 * @param processId the processId to set
	 */
	public void setProcessId(int processId) {
		this.processId = processId;
	}

	/**
	 * @return the userGroupId
	 */
	public int getUserGroupId() {
		return userGroupId;
	}

	/**
	 * @param userGroupId the userGroupId to set
	 */
	public void setUserGroupId(int userGroupId) {
		this.userGroupId = userGroupId;
	}

	/**
	 * @return the userId
	 */
	public int getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(int userId) {
		this.userId = userId;
	}
}
