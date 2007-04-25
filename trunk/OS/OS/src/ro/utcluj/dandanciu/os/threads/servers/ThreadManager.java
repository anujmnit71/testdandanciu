package ro.utcluj.dandanciu.os.threads.servers;

import java.util.Arrays;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import ro.utcluj.dandanciu.nachos.common.ConfigOptions;
import ro.utcluj.dandanciu.os.threads.KernelCallType;
import ro.utcluj.dandanciu.os.threads.ThreadInfo;
import ro.utcluj.dandanciu.os.threads.XThread;
import ro.utcluj.dandanciu.os.threads.tasks.SystemTask;
import ro.utcluj.dandanciu.os.threads.util.InfoType;

public class ThreadManager {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(ThreadManager.class);

	private static int counter = 0;

	private static final class ThreadManagerEntry implements
			Comparable<ThreadManagerEntry> {
		private int id = counter++;

		public XThread theThread;

		public ThreadInfo info;

		public int readyWaiting;

		public String toString() {
			return theThread.getName();
		}

		public int compareTo(ThreadManagerEntry arg0) {
			return id - arg0.id;
		}
	}

	private static final class Ready2RunComparator implements
			Comparator<ThreadManagerEntry> {

		public int compare(ThreadManagerEntry e1, ThreadManagerEntry e2) {
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
			Comparator<ThreadManagerEntry> {

		public int compare(ThreadManagerEntry e1, ThreadManagerEntry e2) {
			if (e1.info.getUsedTicks() > e2.info.getUsedTicks()) {
				return 1;
			} else if (e1.info.getUsedTicks() < e2.info.getUsedTicks()) {
				return -1;
			}
			return e1.compareTo(e2);
		}
	}

	private static SortedSet<ThreadManagerEntry> ready = new TreeSet<ThreadManagerEntry>(
			new Ready2RunComparator());

	private static ThreadManagerEntry[] running = new ThreadManagerEntry[ConfigOptions.NoOfProcs];

	public static XThread next2Run() {
		if (!ready.isEmpty()) {
			logger.info("BEFORE: " + ready.size());
			ThreadManagerEntry entry = ready.first();
			ready.remove(entry);
			logger.info("AFTHER: " + ready.size());
			return entry.theThread;
		}
		return null;
	}

	public static void addReadyThread(XThread thread) {
		logger.info("READY: "+thread.getName());
		ThreadManagerEntry entry = new ThreadManagerEntry();
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
	 *     enter_region:
	 *       TSL REGISTER,LOCK  |copy LOCK to register and set LOCK to 1
	 *       CMP REGISTER,#0    |was LOCK zero?
	 *       JNE ENTER_REGION   |if it was non zero, LOCK was set, so loop
	 *       RET                |return to caller; critical region entered
	 *    &lt;pre&gt;
	 * 
	 */
	public static void enterRegion() {
		while (!(Boolean) SystemTask.getInstance().kernellCall(
				KernelCallType.SYS_TSL, new Object[] { tsl }))
			;
	}

	/**
	 * <pre>
	 *     leave_region:
	 *       MOVE LOCK,#0             |store a 0 in LOCK
	 *       RET                      |return to caller
	 *       &lt;pre&gt;
	 * 
	 */
	public static void exitRegion() {
		tsl = false;
	}

	public static void tick() {
		//logger.info("TICK: \n" + toStringStatic());
		// go through the running positions
		for (int i = 0; i < running.length; i++) {
			ThreadManagerEntry entry = running[i];
			if (entry != null) {
				// logger.info("Used ticks: "+entry.info.getUsedTicks());
				// we have a thread running
				if (entry.info.getTicks() < entry.info.getUsedTicks()) {
					// the thread still has ticks left
					entry.info.setUsedTicks(entry.info.getUsedTicks() + 1);
					continue; // go to next thread
				}
				// no more time for this thread
				// it must be susspended and added to the ready queue
				if (!ready.isEmpty()) {
					ThreadManagerEntry nextEntry = ready.first();
					ready.remove(nextEntry);
					logger.info("AFTER: " + ready.size());
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
					logger.info("BEFORE: " + ready.size());
					ThreadManagerEntry nextEntry = ready.first();
					ready.remove(nextEntry);
					logger.info("AFTHER: " + ready.size());
					nextEntry.info.setUsedTicks(0);
					running[i] = nextEntry;
					nextEntry.theThread.resume();
				}
			}
		}

		for (ThreadManagerEntry entry : ready) {
			entry.readyWaiting++;
		}
	}

	/**
	 * Block a thread, for it to get blocked must be in the running
	 * threads, a logger warn message will be sent if the thread is
	 * not amoong the running threads.
	 * @param blockedThread the thread to be blocked
	 */
	public static void blocked(XThread blockedThread) {
		for (int i = 0; i < running.length; i++) {
			ThreadManagerEntry entry = running[i];
			if (entry.theThread.getThreadId() == blockedThread.getThreadId()) {
				logger.info("BLOCKED " + blockedThread.getName());
				running[i] = null;
				break;
			}
		}
	}
	
	public static String toStringStatic() {
		return "\nProcessManager:"
				+ "\n\tRUNNING: " + Arrays.toString(running)
				+ "\n\tREADY: " + ready; 
				
	}
}
