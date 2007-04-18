package ro.utcluj.dandanciu.os.threads.servers;

import org.apache.log4j.Logger;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import ro.utcluj.dandanciu.nachos.common.ConfigOptions;
import ro.utcluj.dandanciu.os.threads.KernelCallType;
import ro.utcluj.dandanciu.os.threads.ThreadInfo;
import ro.utcluj.dandanciu.os.threads.XThread;
import ro.utcluj.dandanciu.os.threads.tasks.SystemTask;
import ro.utcluj.dandanciu.os.threads.util.InfoType;

public class ProcessManager {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(ProcessManager.class);

	private static int counter = 0;

	private static final class ProcessManagerEntry implements
			Comparable<ProcessManagerEntry> {
		private int id = counter++;

		public XThread theThread;

		public ThreadInfo info;

		public int readyWaiting;

		public int compareTo(ProcessManagerEntry arg0) {
			return id - arg0.id;
		}
	}

	private static final class Ready2RunComparator implements
			Comparator<ProcessManagerEntry> {

		public int compare(ProcessManagerEntry e1, ProcessManagerEntry e2) {
			if (e1.info.getEfectivePriority() > e2.info.getEfectivePriority()) {
				return 1;
			} else if (e1.info.getEfectivePriority() < e2.info
					.getEfectivePriority()) {
				return -1;
			} else if (e1.info.getEfectivePriority() == e2.info
					.getEfectivePriority()
					&& e1.readyWaiting < e2.readyWaiting) {
				return 1;
			}
			return e1.compareTo(e2);
		}
	}

	private static final class RunningComparator implements
			Comparator<ProcessManagerEntry> {

		public int compare(ProcessManagerEntry e1, ProcessManagerEntry e2) {
			if (e1.info.getUsedTicks() > e2.info.getUsedTicks()) {
				return 1;
			} else if (e1.info.getUsedTicks() < e2.info.getUsedTicks()) {
				return -1;
			}
			return e1.compareTo(e2);
		}
	}

	private static SortedSet<ProcessManagerEntry> ready = new TreeSet<ProcessManagerEntry>(
			new Ready2RunComparator());

	private static ProcessManagerEntry[] running = new ProcessManagerEntry[ConfigOptions.NoOfProcs];

	public static XThread next2Run() {
		if (!ready.isEmpty()) {
			ProcessManagerEntry entry = ready.first();
			ready.remove(entry);
			return entry.theThread;
		}
		return null;
	}

	public static void addReadyThread(XThread thread) {
		ProcessManagerEntry entry = new ProcessManagerEntry();
		entry.theThread = thread;
		entry.info = (ThreadInfo) SystemTask.getInstance().kernellCall(
				KernelCallType.SYS_GETINFO,
				new Object[] { InfoType.THREAD, thread.getThreadId() });
		entry.readyWaiting = 0;
		ready.add(entry);
	}

	private static boolean tsl = false;

	/**
	 * <pre>
	 *    enter_region:
	 *      TSL REGISTER,LOCK  |copy LOCK to register and set LOCK to 1
	 *      CMP REGISTER,#0    |was LOCK zero?
	 *      JNE ENTER_REGION   |if it was non zero, LOCK was set, so loop
	 *      RET                |return to caller; critical region entered
	 *   &lt;pre&gt;
	 * 
	 */
	public static void enterRegion() {
		while (!(Boolean) SystemTask.getInstance().kernellCall(
				KernelCallType.SYS_TSL, new Object[] { tsl }))
			;
	}

	/**
	 * <pre>
	 *    leave_region:
	 *      MOVE LOCK,#0             |store a 0 in LOCK
	 *      RET                      |return to caller
	 *      &lt;pre&gt;
	 * 
	 */
	public static void exitRegion() {
		tsl = false;
	}

	public static void tick() {
		logger.info("TICK");
		// go through the running positions
		for (int i = 0; i < running.length; i++) {
			ProcessManagerEntry entry = running[i];
			if (entry != null) {
				// we have a thread running
				if (entry.info.getTicks() < entry.info.getUsedTicks()) {
					// the thread still has ticks left
					entry.info.setUsedTicks(entry.info.getUsedTicks() + 1);
					continue; // go to next thread
				}
				// no more time for this thread
				// it must be susspended and added to the ready queue
				if (!ready.isEmpty()) {
					ProcessManagerEntry nextEntry = ready.first();
					ready.remove(nextEntry);
					nextEntry.info.setUsedTicks(0);
					running[i] = nextEntry;

					nextEntry.theThread.switchFrom(entry.theThread);

					entry.readyWaiting = 0;
					ready.add(entry);
				} else {
					// the currnet process can still run
					entry.info.setUsedTicks(entry.info.getUsedTicks() + 1);
				}
			} else {
				if (!ready.isEmpty()) {
					ProcessManagerEntry nextEntry = ready.first();
					ready.remove(nextEntry);
					nextEntry.info.setUsedTicks(0);
					running[i] = nextEntry;
					nextEntry.theThread.resume();
				}
			}
		}

		for (ProcessManagerEntry entry : ready) {
			entry.readyWaiting++;
		}
	}
}
