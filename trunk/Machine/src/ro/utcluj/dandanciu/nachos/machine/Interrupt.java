package ro.utcluj.dandanciu.nachos.machine;

public class Interrupt {

	private Device device;

	private int priority;

	/**
	 * Hold the id of the local apic currently servicing a pending request for
	 * this interrupt
	 * 
	 * If there is no request for this interrupt apicId should be set to -1
	 */
	private int apicId = -1;

	private PhysicalException cause;

	/**
	 * @return the priority
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * @param priority
	 *            the priority to set
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}

	/**
	 * @return the device
	 */
	public Device getDevice() {
		return device;
	}

	/**
	 * Hold the id of the local apic currently servicing a pending request for
	 * this interrupt
	 * 
	 * If there is no request for this interrupt apicId should be set to -1
	 * 
	 * @param device
	 *            the device to set
	 */
	public void setDevice(Device device) {
		this.device = device;
	}

	/**
	 * Hold the id of the local apic currently servicing a pending request for
	 * this interrupt
	 * 
	 * If there is no request for this interrupt apicId should be set to -1
	 * 
	 * @return the apicId
	 */
	public int getApicId() {
		return apicId;
	}

	/**
	 * @param apicId
	 *            the apicId to set
	 */
	public void setApicId(int apicId) {
		this.apicId = apicId;
	}

	public void setCause(PhysicalException exception) {
		this.cause = exception;		
	}

	/**
	 * @return the cause
	 */
	public PhysicalException getCause() {
		return cause;
	}

}
