/**
 * 
 */
package ro.utcluj.dandanciu.nachos.machine;

import org.apache.log4j.Logger;

import java.io.FileOutputStream;
import java.io.RandomAccessFile;

import ro.utcluj.dandanciu.nachos.machine.utils.ConfigOptions;

/**
 * @author Dan Danciu
 * 
 */
public class HardDisk extends Device implements Runnable {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(HardDisk.class);

	private int traks = -1;

	private RandomAccessFile support = null;

	private String name;

	private boolean active;

	private byte[] buffer;

	private int lastSector;

	public HardDisk(String name, Apic apic, int code) {
		super(apic, code);

		this.name = name;
		this.traks = ConfigOptions.DiskNoOfTraks;

		try {
			FileOutputStream fsFile;
			fsFile = new FileOutputStream(name);
			fsFile.close();
			support = new RandomAccessFile(name, "rw");
			support.write(ConfigOptions.DiskFileTypeId); // write magic
			// number

			// need to write at end of file, so that reads will not return EOF
			support.seek(getSize() - 4);
			support.write(ConfigOptions.DiskEOF); 

		} catch (Exception e) {
			logger.error("HardDisk(String)", e); //$NON-NLS-1$

			assert (false); // we cann't get here, if we did IT IS BAD!
		}

	}

	/**
	 * Processes a reading request, takes the content of the specified sector
	 * and stores it in the disks buffer
	 * 
	 * @param sectorNumber
	 * @param data
	 * @param index
	 */
	public void readRequest(int sectorNumber, int index) {
		if (logger.isDebugEnabled()) {
			logger.debug("readRequest(int, int) - start"); //$NON-NLS-1$
		}

		assert (!active); // only one request at a time
		assert ((sectorNumber >= 0) && (sectorNumber < getNumberOfSectors()));

		try {
			byte[] data = new byte[ConfigOptions.DiskSizeOfSector];

			int ticks = computeLatency(sectorNumber);

			//this.wait(ticks * ConfigOptions.TickLenght);

			active = true;
			this.lastSector = sectorNumber;

			support.seek(ConfigOptions.DiskSizeOfSector * sectorNumber
					+ ConfigOptions.DiskFileTypeIdSize);
			support.read(data, index, ConfigOptions.DiskSizeOfSector);
			
			this.buffer = data;

		} catch (Exception e) {
			logger.error("readRequest(int, int)", e); //$NON-NLS-1$

			assert (false);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("readRequest(int, int) - end"); //$NON-NLS-1$
		}
	}

	public void writeRequest(int sectorNumber, int index) {
		if (logger.isDebugEnabled()) {
			logger.debug("writeRequest(int, int) - start"); //$NON-NLS-1$
		}

		assert (!active);
		assert ((sectorNumber >= 0) && (sectorNumber < getNumberOfSectors()));

		try {

			int ticks = computeLatency(sectorNumber);

			//this.wait(ticks * ConfigOptions.TickLenght);
			
			byte[] data = getBuffer();
			
			support.seek(ConfigOptions.DiskSizeOfSector
					* sectorNumber + ConfigOptions.DiskFileTypeIdSize);
			support.write(data, index, ConfigOptions.DiskSizeOfSector);
		
			this.lastSector = sectorNumber;
			active = true;
		
		} catch (Exception e) {
			logger.error("writeRequest(int, int)", e); //$NON-NLS-1$

			assert (false);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("writeRequest(int, int) - end"); //$NON-NLS-1$
		}
	}

	private int getNumberOfSectors() {
		if (logger.isDebugEnabled()) {
			logger.debug("getNumberOfSectors() - start"); //$NON-NLS-1$
		}

		int returnint = traks * ConfigOptions.DiskNoOfSectorsPerTrack;
		if (logger.isDebugEnabled()) {
			logger.debug("getNumberOfSectors() - end"); //$NON-NLS-1$
		}
		return returnint;
	}

	/**
	 * Returns the size of the disk
	 * @return
	 */
	private int getSize() {
		if (logger.isDebugEnabled()) {
			logger.debug("getSize() - start"); //$NON-NLS-1$
		}

		int returnint = ConfigOptions.DiskFileTypeIdSize + traks * ConfigOptions.DiskNoOfSectorsPerTrack * ConfigOptions.DiskSizeOfSector;
		if (logger.isDebugEnabled()) {
			logger.debug("getSize() - end"); //$NON-NLS-1$
		}
		return returnint;
	}

	/**
	 * The total number of tick it is needed to get to the new sector
	 * 
	 * It sums up the period of time to change traks + the time needed to move
	 * over the new sector
	 * 
	 * @param newSector
	 *            the sector the harddisk has to deal with
	 * @param writing
	 * @return
	 */
	private int computeLatency(int newSector) {
		if (logger.isDebugEnabled()) {
			logger.debug("computeLatency(int) - start"); //$NON-NLS-1$
		}

		int seek = timeToSeek(newSector); // computes seek and rotation

		int oldTrack = lastSector / ConfigOptions.DiskNoOfSectorsPerTrack;
		int newTrack = newSector / ConfigOptions.DiskNoOfSectorsPerTrack;

		int timeToChangeTrack = Math.abs(oldTrack - newTrack)
				* ConfigOptions.DiskRotationTime;

		int returnint = (timeToChangeTrack + seek);
		if (logger.isDebugEnabled()) {
			logger.debug("computeLatency(int) - end"); //$NON-NLS-1$
		}
		return returnint;
	}

	/**
	 * Returns the time, in ticks, the head needs to change position from the
	 * old sector to the new one
	 * 
	 * @param newSector
	 *            the sector it has to deal with
	 * @return the time, in ticks, needed to get to the new sector
	 */
	private int timeToSeek(int newSector) {
		if (logger.isDebugEnabled()) {
			logger.debug("timeToSeek(int) - start"); //$NON-NLS-1$
		}

		int newTrack = newSector / ConfigOptions.DiskNoOfSectorsPerTrack;
		int oldTrack = lastSector / ConfigOptions.DiskNoOfSectorsPerTrack;
		int seek = Math.abs(newTrack - oldTrack) * ConfigOptions.DiskSeekTime;
		// how long will seek take?
		if ((seek % ConfigOptions.DiskSeekTime) == 0)
			seek += (seek % ConfigOptions.DiskSeekTime)
					* ConfigOptions.DiskSeekTime;

		if (logger.isDebugEnabled()) {
			logger.debug("timeToSeek(int) - end"); //$NON-NLS-1$
		}
		return seek;
	}

	/**
	 * @return the buffer
	 */
	public byte[] getBuffer() {
		return buffer;
	}

	/**
	 * @param buffer
	 *            the buffer to set
	 */
	public void setBuffer(byte[] buffer) {
		assert (buffer.length > ConfigOptions.DiskSizeOfSector);
		this.buffer = new byte[ConfigOptions.DiskSizeOfSector]; 
		for(int i = 0; i < ConfigOptions.DiskSizeOfSector; i++){
			this.buffer[i] = (i < buffer.length) ? buffer[i] : 0;
		}
	}

	public void run() {
		
	}

	@Override
	public void handle() {
		// TODO Auto-generated method stub
		
	}
}
