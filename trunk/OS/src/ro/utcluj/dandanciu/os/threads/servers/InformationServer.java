package ro.utcluj.dandanciu.os.threads.servers;

import ro.utcluj.dandanciu.nachos.common.ConfigOptions;
import ro.utcluj.dandanciu.os.threads.KernelCallType;
import ro.utcluj.dandanciu.os.threads.structs.TimeInfo;
import ro.utcluj.dandanciu.os.threads.tasks.SystemTask;

/**
 * The information server (IS) handles jobs such as providing debugging and
 * status information about other drivers and servers, something that is more
 * necessary in a system designed for experimentation, than would be the case
 * for a commercial operating system which users cannot alter.
 * 
 * Only one instance is allowed.
 *  
 * @author Dan Danciu
 * 
 */
public class InformationServer {

	/**
	 * The number of seconds since January 1st, 1970 untill booting
	 * 
	 */
	private final long seconds;
	
	/**
	 * Constructs the Information Server, must give the time of boot
	 * @param seconds the time of the booting process
	 */
	private InformationServer(long seconds){
		this.seconds = seconds;		
	}

	/**
	 * Returns the number of seconds since January 1st, 1970 0000 hours.
	 * @return The number of seconds since January 1st, 1970 0000 hours.
	 */
	public long currnetTimeInMiliseconds() {
		TimeInfo ti = (TimeInfo) SystemTask.getInstance().kernellCall(
				KernelCallType.SYS_TIMES, new Object[0]);
		return (seconds * 1000) + (ti.ticks / ConfigOptions.TickLenght);
	}
	
	private static boolean instanciated = false;
	public static InformationServer getInformationServer(long seconds){
		if(instanciated) {
			SystemTask.getInstance().kernellCall(KernelCallType.SYS_ABORT, null);
		}
		instanciated = true;
		return new InformationServer(seconds);
	}

}
