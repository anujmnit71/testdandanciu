package ro.utcluj.dandanciu.nachos.machine.utils;

public class ConfigOptions {

	/**
	 * Definitions related to the size, and format of user memory
	 */
	public static final int PageSize = 128;

	public static final int NoPhysPages = 128;

	/**
	 * Indicates the number of processors this machine has
	 * <br />
	 * Default value: <code>2</code>
	 */
	public static final int NoOfProcs = 2;

	/**
	 * Holds the number of registers a processor has
	 * <br/>
	 * Default value: <code>40</code>
	 */
	public static final int NoOfRegisters = 41;

	/**
	 * The period of time (miliseconds) between two ticks
	 * <br/>
	 * Default value: 1000 (miliseconds)
	 */
	public static final int TickLenght = 1000;

	/**
	 * Flag for indecating if we should suspend after each simulate instruction
	 */
	public static boolean singleStep = false;

	/**
	 * After this period of time (ticks) we should suspend and debug
	 * <br/>
	 * A value of 0 (zero) sais this options is disabled
	 */
	public static int runUntilTime = 0;
}
