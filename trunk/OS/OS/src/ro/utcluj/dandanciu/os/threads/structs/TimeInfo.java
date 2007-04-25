package ro.utcluj.dandanciu.os.threads.structs;

/**
 * Represents the struct return in the SYS_TIMES KernelCall<br>
 * Has the following fields:<br>
 * <ul>
 * 	<li>ticks - the number of ticks since boot</li>
 * </ul>
 * @author Dan Danciu
 *
 */
public class TimeInfo {
	
	public long ticks = 0; 
	
}
