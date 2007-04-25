package ro.utcluj.dandanciu.os.threads.kernel;

public interface GenericKernel {
	
	
	/**
	 * This menthod should be called when the OS starts.
	 */
	void initialize();
	
	/**
	 * This must be called when when the OS is finishing.
	 *
	 */
	void terminate();

}
