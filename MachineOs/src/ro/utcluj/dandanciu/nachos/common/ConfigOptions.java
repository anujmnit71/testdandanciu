package ro.utcluj.dandanciu.nachos.common;

public class ConfigOptions {

	/**
	 * Definitions related to the size, and format of user memory
	 */
	public static final int PageSize = 128;

	public static final int NoPhysPages = 128;

	/**
	 * Indicates the number of processors this machine has <br />
	 * Default value: <code>2</code>
	 */
	public static final int NoOfProcs = 2;

	/**
	 * Holds the number of registers a processor has <br/> Default value:
	 * <code>41</code>
	 */
	public static final int NoOfRegisters = 41;

	/**
	 * The period of time (miliseconds) between two ticks <br/> Default value:
	 * 100 (miliseconds)
	 */
	public static final int TickLenght = 3;

	/**
	 * The number of sectors a hardisk trak has <br/> Default value: 32
	 */
	public static final int DiskNoOfSectorsPerTrack = 32;

	/**
	 * Size in Bytes of a hardisk sector <br/> Default value: 128
	 */
	public static final int DiskSizeOfSector = 128;

	/**
	 * The size of the String represeting the id of the file type for emulating
	 * the hardisk
	 */
	public static final int DiskFileTypeIdSize = 6;

	/**
	 * The size of the String represeting the id of the file type for emulating
	 * the hardisk
	 */
	public static final byte[] DiskFileTypeId = { 0x6e, 0x61 , 0x63, 0x68, 0x6f , 0x73};

	public static final int DiskNoOfTraks = 6;

	public static final int DiskSeekTime = 100;

	public static final int DiskRotationTime = 10;

	public static final byte[] DiskEOF = { 0x55 };

	/**
	 * Virtual Memory Page Size
	 * Default value = 4k = 2^12;
	 */
	public static final int VMPageSize = 4096;

	/**
	 * Flag for indecating if we should suspend after each simulate instruction
	 */
	public static boolean singleStep = false;

	/**
	 * After this period of time (ticks) we should suspend and debug <br/> A
	 * value of 0 (zero) sais this options is disabled
	 */
	public static int runUntilTime = 0;
}
