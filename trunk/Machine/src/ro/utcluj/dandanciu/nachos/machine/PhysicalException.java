package ro.utcluj.dandanciu.nachos.machine;

public class PhysicalException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4439900010568385502L;

	private int type;
	
	private String name;
	
	private String description;	
	
	public static PhysicalException NO_EXCEPTION = new PhysicalException(0,
			"no exception", "");
	
	public static PhysicalException SYSCALL_EXCEPTION = new PhysicalException(0,
			"syscall exception", "");
	
	public static PhysicalException PAGE_FAULT_EXCEPTION = new PhysicalException(0,
			"page fault exception", "");
	
	public static PhysicalException READ_ONLY_EXCEPTION = new PhysicalException(0,
			"read only exception", "");
	
	public static PhysicalException BUSS_ERROR_EXCEPTION = new PhysicalException(0,
			"buss error exception", "");
	
	public static PhysicalException ADDRESS_ERROR_EXCEPTION = new PhysicalException(0,
			"address error exception", "");
	
	public static PhysicalException OVERFLOW_EXCEPTION = new PhysicalException(0,
			"overflow exception", "");

	public static PhysicalException ILLEGAL_INSTRUCTION_EXCEPTION = new PhysicalException(0,
			"illegal instruction exception", "");
	
	private PhysicalException(int type, String name, String description) {
		super();
		this.type = type;
		this.name = name;
		this.description = description;
	}
	
	public InterruptRequest getInterruptRequest(Processor processor){
		InterruptRequest rq = new InterruptRequest();
		Interrupt interrupt = new Interrupt();
		interrupt.setDevice(null);
		interrupt.setCause(this);
		rq.setInterrupt(interrupt);
		rq.setStatus(-1);
		
		return rq;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	public void handle() {
		// TODO add handle exception code
		
	}
}
