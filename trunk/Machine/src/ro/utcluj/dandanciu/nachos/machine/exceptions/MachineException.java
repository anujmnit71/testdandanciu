package ro.utcluj.dandanciu.nachos.machine.exceptions;

/**
 * 
 * @author Dan Danciu
 *
 */
public class MachineException extends Exception {
	private static final long serialVersionUID = -7690459041397761267L;

	private int exception;

	public MachineException(String message, int exceptionNumber) {
		super(message);
		exception = exceptionNumber;
	}

	public int getException() {
		return exception;
	}

	public void setException(int exception) {
		this.exception = exception;
	}
}
