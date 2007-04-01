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
	
	/**
	 * Parent process PID
	 */
	private int pid;
	
	/**
	 * Process group (GID)
	 */
	private int group;
	
	/**
	 * Children's CPU time
	 */
	private int childrensTicks = 0;
	
	/**
	 * Real UID
	 */
	private int realUid;
	
	/**
	 * Effective UID
	 */
	private int uid;
	
	/**
	 * Real GID
	 */
	private int realGid;
	
	/**
	 * Effective GID
	 */
	private int gid;

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
	 * @return the gid
	 */
	public int getGid() {
		return gid;
	}

	/**
	 * @param gid the gid to set
	 */
	public void setGid(int gid) {
		this.gid = gid;
	}

	/**
	 * @return the group
	 */
	public int getGroup() {
		return group;
	}

	/**
	 * @param group the group to set
	 */
	public void setGroup(int group) {
		this.group = group;
	}

	/**
	 * @return the pid
	 */
	public int getPid() {
		return pid;
	}

	/**
	 * @param pid the pid to set
	 */
	public void setPid(int pid) {
		this.pid = pid;
	}

	/**
	 * @return the realGid
	 */
	public int getRealGid() {
		return realGid;
	}

	/**
	 * @param realGid the realGid to set
	 */
	public void setRealGid(int realGid) {
		this.realGid = realGid;
	}

	/**
	 * @return the realUid
	 */
	public int getRealUid() {
		return realUid;
	}

	/**
	 * @param realUid the realUid to set
	 */
	public void setRealUid(int realUid) {
		this.realUid = realUid;
	}

	/**
	 * @return the uid
	 */
	public int getUid() {
		return uid;
	}

	/**
	 * @param uid the uid to set
	 */
	public void setUid(int uid) {
		this.uid = uid;
	}
	
	/**
	 * File info for sharing text
	 */
	
	/**
	 * Bitmaps for signals
	 */
	
	/**
	 * Various flag bits
	 */
}
