package ro.utcluj.dandanciu.os.threads;

public class ThreadInfo {

	/**
	 * Registers
	 */
	private int[] registers;
	
	/**
	 * Program counter
	 */
	private int pc;
	/**
	 * Program status word
	 */
	private long status;
	/**
	 * Stack pointer
	 */
	private int stackAddress;
	/**
	 * Process state
	 */
	private ThreadState state = ThreadState.UNKNOWN;
	/**
	 * Thread scheduling priority
	 */
	private int priority = 0;
	
	
	/**
	 * Maximum scheduling priority
	 */
	private int schedulingPriority = 0;
	/**
	 * Scheduling ticks left
	 */
	private int ticks = -1;
	/**
	 * CPU time used 
	 */
	private int usedTicks = 0;
	/**
	 * Message queue pointers
	 */
	
	/**
	 * Pending signal bits
	 */
	/**
	 * Various flag bits
	 */
	/**
	 * Process name
	 */
	private String name;
	
	public ThreadInfo(String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the pc
	 */
	public int getPc() {
		return pc;
	}

	/**
	 * @param pc the pc to set
	 */
	public void setPc(int pc) {
		this.pc = pc;
	}

	/**
	 * @return the priority
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * @param priority the priority to set
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}

	/**
	 * @return the registers
	 */
	public int[] getRegisters() {
		return registers;
	}

	/**
	 * @param registers the registers to set
	 */
	public void setRegisters(int[] registers) {
		this.registers = registers;
	}

	/**
	 * @return the schedulingPriority
	 */
	public int getSchedulingPriority() {
		return schedulingPriority;
	}

	/**
	 * @param schedulingPriority the schedulingPriority to set
	 */
	public void setSchedulingPriority(int schedulingPriority) {
		this.schedulingPriority = schedulingPriority;
	}

	/**
	 * @return the stackAddress
	 */
	public int getStackAddress() {
		return stackAddress;
	}

	/**
	 * @param stackAddress the stackAddress to set
	 */
	public void setStackAddress(int stackAddress) {
		this.stackAddress = stackAddress;
	}

	/**
	 * @return the state
	 */
	public ThreadState getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(ThreadState state) {
		this.state = state;
	}

	/**
	 * @return the status
	 */
	public long getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(long status) {
		this.status = status;
	}

	/**
	 * @return the ticks
	 */
	public int getTicks() {
		return ticks;
	}

	/**
	 * @param ticks the ticks to set
	 */
	public void setTicks(int ticks) {
		this.ticks = ticks;
	}

	/**
	 * @return the usedTicks
	 */
	public int getUsedTicks() {
		return usedTicks;
	}

	/**
	 * @param usedTicks the usedTicks to set
	 */
	public void setUsedTicks(int usedTicks) {
		this.usedTicks = usedTicks;
	}
}
