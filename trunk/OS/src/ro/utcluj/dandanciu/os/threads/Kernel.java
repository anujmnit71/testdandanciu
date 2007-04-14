package ro.utcluj.dandanciu.os.threads;

public class Kernel extends XThreadAbstract{
	
	private static Kernel kernel = null;
	
	private ProcessInfo[] proccessTabls;
	
	private ClockTask clockTask;
	
	private SystemTask systemTask;

	/**
	 * @return the systemTask
	 */
	public SystemTask getSystemTask() {
		return systemTask;
	}

	/**
	 * @param systemTask the systemTask to set
	 */
	public void setSystemTask(SystemTask systemTask) {
		this.systemTask = systemTask;
	}

	public static Kernel getKernel(){
		return kernel;
	}
	
	@Override
	public void run() {
		systemTask = new SystemTask();
		clockTask = new ClockTask();
	}

	public static void fork(XThreadAbstract chield, XThreadAbstract parent) {
		// TODO add the work of the kernel in a fork
		
	}



	@Override
	protected void restoreContext() {
	}

	@Override
	protected void saveContext() {
	}



	/**
	 * @return the clockTask
	 */
	public ClockTask getClockTask() {
		return clockTask;
	}



	/**
	 * @param clockTask the clockTask to set
	 */
	public void setClockTask(ClockTask clockTask) {
		this.clockTask = clockTask;
	}
}
