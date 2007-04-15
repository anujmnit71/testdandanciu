package ro.utcluj.dandanciu.os.threads.servers;

import java.util.SortedSet;
import java.util.TreeSet;

import ro.utcluj.dandanciu.os.threads.KernelCallType;
import ro.utcluj.dandanciu.os.threads.ProcessManagementInfo;
import ro.utcluj.dandanciu.os.threads.XThread;
import ro.utcluj.dandanciu.os.threads.tasks.SystemTask;

public class ProcessManager {
	
	private static class Ready2RunEntry implements Comparable {
		public XThread theThread;
		public ProcessManagementInfo  pmInfo;
		public int compareTo(Object arg0) {
			int op = pmInfo.getEfectivePriority();
			int cp = ((Ready2RunEntry) arg0).pmInfo.getEfectivePriority();
			if(op > cp) {
				return 1;
			} else if (op < cp) {
				return -1;
			}
			return 0;			
		}
	}

	private static SortedSet<Ready2RunEntry> ready2Run = new TreeSet<Ready2RunEntry>();
	
	public static XThread next2Run(){
		Ready2RunEntry entry = ready2Run.first();
		ready2Run.remove(entry);		
		return entry.theThread;
	}
	
	public static void addReadyThread(XThread thread){
		Ready2RunEntry entry = new Ready2RunEntry();
		entry.theThread = thread;
		entry.pmInfo = null; 
		//TODO: get the info from the kernel
	}
	
	private static boolean tsl = false;
	/**
	 * <pre>
	 * enter_region:
     *   TSL REGISTER,LOCK  |copy LOCK to register and set LOCK to 1
     *   CMP REGISTER,#0    |was LOCK zero?
     *   JNE ENTER_REGION   |if it was non zero, LOCK was set, so loop
     *   RET                |return to caller; critical region entered
	 *<pre>
	 */
	public static void enterRegion() {
		while (!(Boolean) SystemTask.getInstance().kernellCall(KernelCallType.SYS_TSL,
				new Object[] { tsl }))
			;
	}
	
	/**
	 * <pre>
	 * leave_region:
     *   MOVE LOCK,#0             |store a 0 in LOCK
     *   RET                      |return to caller
     *   <pre>
	 */
	public static void exitRegion(){
		tsl = false;
	}
}
