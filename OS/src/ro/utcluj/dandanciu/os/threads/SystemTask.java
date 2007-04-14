package ro.utcluj.dandanciu.os.threads;


/**
 * 
 * Message type From Meaning
 * 
 * <code>sys_fork<code> PM A process has forked
 * 
 * sys_exec PM Set stack pointer after EXEC call
 * 
 * sys_exit PM A process has exited
 * 
 * sys_nice PM Set scheduling priority
 * 
 * sys_privctl RS Set or change privileges
 * 
 * sys_trace PM Carry out an operation of the PTRACE call
 * 
 * sys_kill PM, FS, TTY Send signal to a process after KILL call
 * 
 * sys_getksig PM PM is checking for pending signals
 * 
 * sys_endksig PM PM has finished processing signal
 * 
 * sys_sigsend PM Send a signal to a process
 * 
 * sys_sigreturn PM Cleanup after completion of a signal
 * 
 * sys_irqctl Drivers Enable, disable, or configure interrupt
 * 
 * sys_devio Drivers Read from or write to an I/O port
 * 
 * sys_sdevio Drivers Read or write string from/to I/O port
 * 
 * sys_vdevio Drivers Carry out a vector of I/O requests
 * 
 * sys_int86 Drivers Do a real-mode BIOS call
 * 
 * sys_newmap PM Set up a process memory map
 * 
 * sys_segctl Drivers Add segment and get selector (far data access)
 * 
 * sys_memset PM Write char to memory area
 * 
 * sys_umap Drivers Convert virtual address to physical address
 * 
 * sys_vircopy FS, Drivers Copy using pure virtual addressing
 * 
 * sys_physcopy Drivers Copy using physical addressing
 * 
 * sys_virvcopy Any Vector of VCOPY requests
 * 
 * sys_physvcopy Any Vector of PHYSCOPY requests
 * 
 * sys_times PM Get uptime and process times
 * 
 * sys_setalarm PM, FS, Drivers Schedule a synchronous alarm
 * 
 * sys_abort PM, TTY Panic: MINIX is unable to continue
 * 
 * sys_getinfo Any Request system information
 * 
 * 
 * @author Dan Danciu
 *
 */
public class SystemTask {
	
	public SystemTask(){
		setUp();
	}

	private void setUp() {
		kernelCalls = new KernelCall[KernelCallType.values().length];
		
		setKernelCall(KernelCallType.SYS_TSL, new KernelCall() {
			public synchronized Object call(Object[] params) {
				Boolean value = ((Boolean) params[0]);
				if (value)
					return Boolean.FALSE;
				value = true;
				return Boolean.TRUE;
			}
		});
		
		/**
		 * Makes the fork call to the Kernel
		 */
		setKernelCall(KernelCallType.SYS_FORK, new KernelCall() {
			
			/**
			 * Implementation of the fork call to the kernel
			 *@param params array of Objects, param[0] - the forking thread
			 * and param[1] - the thread from which the fork is being 
			 * done
			 * @return Returns <code>null</code> no matter what 
			 */
			public Object call(Object[] params) {
				//implement fork kernel call
				Kernel.fork((XThreadAbstract)params[0], (XThreadAbstract)params[1]);
				return null;
			}
		});
		
		setKernelCall(KernelCallType.SYS_SETALARM, new KernelCall() {

			public Object call(Object[] params) {
				Kernel.getKernel().getClockTask().addAlarm(
						((Long) params[0]).longValue(), 
						(Runnable) params[1]);
				return null;
			}
			
		});
		
	}

	/**
	 * The array of kernel call.
	 */
	private KernelCall[] kernelCalls;

	/**
	 * Set a kernel call method
	 * 
	 * @param type
	 *            which kernel call we want to change
	 * @param call
	 *            the new kernel call we want to set
	 * @return the old kernel call, the one we are replacing
	 */
	public KernelCall setKernelCall(KernelCallType type, KernelCall call) {
		KernelCall old = kernelCalls[type.ordinal()];
		kernelCalls[type.ordinal()] = call;
		return old;
	}

	/**
	 * Make a kernel call
	 * 
	 * @param type
	 *            which kernel call we are making
	 * @param params
	 *            the array of parameters
	 * @return the return value of the kernel call
	 */
	public Object kernellCall(KernelCallType type, Object[] params) {
		return kernelCalls[type.ordinal()].call(params);
	}
	
	public static SystemTask getInstance(){
		return Kernel.getKernel().getSystemTask();
	}

}
