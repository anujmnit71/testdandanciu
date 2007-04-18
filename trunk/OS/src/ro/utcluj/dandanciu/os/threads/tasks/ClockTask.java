package ro.utcluj.dandanciu.os.threads.tasks;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import ro.utcluj.dandanciu.nachos.machinetoos.Interruptable;
import ro.utcluj.dandanciu.nachos.ostomachine.InterruptServiceRoutineInterface;
import ro.utcluj.dandanciu.os.threads.servers.ProcessManager;

public class ClockTask implements InterruptServiceRoutineInterface {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(ClockTask.class);

	/**
	 * Holds the current time in ticks. Its value is the number of ticks since
	 * system has booted.
	 * 
	 * The time of the boot is hold by the InfoServer (seconds since January
	 * 1st, 1970)
	 */
	private static long realtime = 0;

	/**
	 * Shows when the kernel should change the currently running thread
	 */
	private static long next_switching = 0;

	/**
	 * Represents an entry in the alarms queue.
	 * 
	 * Each entry's time left holds the number of ticks which are left after the
	 * previous entry has finished
	 * 
	 * @author Dan Danciu
	 * 
	 */
	private static int alarmEntryCounter = 0;

	private final class AlarmEntry implements Comparable<AlarmEntry> {
		private int id = alarmEntryCounter++;

		public long timeLeft;

		public Runnable target;

		public int compareTo(AlarmEntry arg0) {
			if (timeLeft > arg0.timeLeft)
				return 1;
			else if (timeLeft < arg0.timeLeft)
				return -1;
			else
				return (id - arg0.id);
		}
	}

	/**
	 * The alarms which are scheduled.
	 */
	private static SortedSet<AlarmEntry> alarms = new TreeSet<AlarmEntry>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see ro.utcluj.dandanciu.os.threads.tasks.InterruptServiceInterface#doHandle()
	 */
	public void doHandle(Interruptable interrupt) {
		ProcessManager.enterRegion();
		// adjust realtime
		realtime++;
		// increment the current thread's ticks, also will check if any of
		// the threads should be switched
		ProcessManager.tick();
		/*
		 * Check if there are alarms to be called. If not just decrement the
		 * timeLeft for the first entry.
		 */
		boolean flag = true;
		while (flag) {
			if (!alarms.isEmpty()) {
				AlarmEntry first = alarms.first();
				if (first.timeLeft == 0) {
					alarms.remove(first);
					System.out.println("alarm.run");
					first.target.run();
				} else {
					first.timeLeft--;
					flag = false;
				}
			}
		}
		ProcessManager.exitRegion();
	}

	public static void reset() {
		realtime = 0;
		alarms = new TreeSet<AlarmEntry>();
	}

	/**
	 * Adds an alarm. After <code>offset<code> ticks the c<code>SystemTask<code>
	 * will be signal to run the target.
	 * @param offset the number of ticks to wait to triger the target
	 * @param target the target to be run
	 */
	public void addAlarm(long offset, Runnable target) {
		AlarmEntry entry = new AlarmEntry();
		entry.target = target;
		if (alarms.isEmpty()) {
			entry.timeLeft = offset;
			alarms.add(entry);
			return;
		}

		Iterator<AlarmEntry> it = alarms.iterator();
		/*
		 * Compute the offset for this entry. Each entry's time left holds the
		 * number of ticks which are left after the previous entry has finished
		 */
		while (it.hasNext()) {
			AlarmEntry current = it.next();

			if (offset - current.timeLeft < 0) {
				entry.timeLeft = offset;
				alarms.add(entry);
				break;
			} else {
				offset = offset - current.timeLeft;
			}
		}
		/*
		 * Decrement the remaing entries time left by the offset of the new
		 * entry
		 */
		while (it.hasNext()) {
			it.next().timeLeft -= offset;
		}
	}

	/**
	 * @return the realtime
	 */
	public static long getRealtime() {
		return realtime;
	}
}
