package ro.utcluj.dandanciu.os.threads;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import ro.utcluj.dandanciu.os.utils.OsConfigOptions;

public class ClockTask {
	/**
	 * Holds the current time in ticks.
	 * Its value is the number of ticks since system has booted.
	 * 
	 * The time of the boot is hold by the 
	 * InfoServer (seconds since January 1st, 1970)
	 */
	private static long realtime = 0;
	
	/**
	 * Shows when the kernel should change the currently running thread
	 */
	private static long next_switching = 0;
	
	/**
	 * Represents an entry in the alarms queue.
	 * 
	 * Each entry's time left holds the
	 * number of ticks which are left after 
	 * the previous entry has finished
	 * 
	 * @author Dan Danciu
	 * 
	 */
	private class AlarmEntry implements Comparable<AlarmEntry> {
		public int timeLeft;
		public Runnable target;
		
		public int compareTo(AlarmEntry arg0) {
			if(timeLeft > arg0.timeLeft)
				return 1;
			else 
				return -1;
		}		
	}
	
	/**
	 * The alarms which are scheduled.
	 */
	private static SortedSet<AlarmEntry> alarms = new TreeSet<AlarmEntry>();
	
	public static void doHandle() {
		//adjust realtime
		realtime++;
		//increment the current thread's ticks
		//TODO: SystemTask.incrementThreadsTicks
		
		/*
		 * Check if there are alarms to be called.
		 * If not just decrement the timeLeft for the first entry. 
		 */
		AlarmEntry first = alarms.first();
		if(first.timeLeft == 0) {
			alarms.remove(first);
			//TODO: SystemTask.ready(first.target)			
		} else {
			first.timeLeft--;
		}
		
		/*
		 * Chech if the process scheduler should be notified
		 */
		if(realtime == next_switching) {
			next_switching += OsConfigOptions.ThreadQuantom;
			//TODO: SystemTask.switch
		}
	
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
	void addAlarm(int offset, Runnable target) {
		AlarmEntry entry = new AlarmEntry();
		Iterator<AlarmEntry> it = alarms.iterator();
		/*
		 * Compute the offset for this entry.
		 * Each entry's time left holds the number of
		 * ticks which are left after the previous entry
		 * has finished
		 */
		while(it.hasNext()) {
			AlarmEntry current = it.next();
			
			if(offset - current.timeLeft < 0) {
				entry.timeLeft = offset;
				alarms.add(entry);
				break;
			} else {
				offset = offset - current.timeLeft;
			}
		}
		/*
		 * Decrement the remaing entries time left by the offset of the 
		 * new entry
		 */
		while(it.hasNext()) {
			it.next().timeLeft -= offset;
		}
	}
	
	
	 
	

}
