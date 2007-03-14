/**
 * 
 */
package ro.utcluj.dandanciu.nachos.machine;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import ro.utcluj.dandanciu.nachos.machine.utils.ConfigOptions;

/**
 * @author Dan Danciu
 * 
 */
public class HardDisk extends Device {

	private int traks = -1;

	private RandomAccessFile support = null;
	
	private String name;

	public HardDisk(String name) {
		
		this.name = name;
		this.traks = ConfigOptions.DiskNoOfTraks;
		
		

		try {
			FileOutputStream fsFile;
			fsFile = new FileOutputStream(name);
			fsFile.close();
			support = new RandomAccessFile(name, "rw");
			support.write(ConfigOptions.DiskFileTypeId); // write magic number

			// need to write at end of file, so that reads will not return EOF
			support.seek(getSize() - 4);
			support.writeInt(0);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ro.utcluj.dandanciu.nachos.machine.Device#handleInterruput()
	 */
	@Override
	public void handleInterruput() {

	}

	private int getSize() {
		return ConfigOptions.DiskFileTypeIdSize + traks
				* ConfigOptions.DiskNoOfSectorsPerTrack
				* ConfigOptions.DiskSizeOfSector;
	}

}
