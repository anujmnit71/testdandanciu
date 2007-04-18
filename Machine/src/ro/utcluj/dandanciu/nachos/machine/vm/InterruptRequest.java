package ro.utcluj.dandanciu.nachos.machine.vm;

import ro.utcluj.dandanciu.nachos.machine.Interrupt;

public class InterruptRequest implements Comparable<InterruptRequest> {
	
	private Interrupt interrupt;
	
	/**
	 * Status<br/>
	 */
	private int status;
	
	public static final int PENDING = 1;
	public static final int BEING_SERVICED = 1;
	public static final int DONE = 1;
		

	/**
	 * @return the interrupt
	 */
	public Interrupt getInterrupt() {
		return interrupt;
	}

	/**
	 * @param interrupt the interrupt to set
	 */
	public void setInterrupt(Interrupt interrupt) {
		this.interrupt = interrupt;
	}

	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * Status meaning:
	 * <ul>
	 * <li> -1: pending</li>
	 * <li>  0: processing</li>
	 * <li>  1: done</li>
	 * </ul>
	 * @param status the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	public int compareTo(InterruptRequest o) {
		if(this.interrupt.getPriority() > o.interrupt.getPriority())
			return 1;
		return -1;	
	}

}
