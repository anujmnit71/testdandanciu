package ro.utcluj.dandanciu.nachos.machine.vm;

import ro.utcluj.dandanciu.nachos.machine.Memory;
import ro.utcluj.dandanciu.nachos.machine.PhysicalException;
import ro.utcluj.dandanciu.nachos.machine.exceptions.IllegalWordSizeException;


public interface LocalMemoryManagementUnit {

	void write(int asid, int virtualAddress, int size, int value, boolean isData) throws IllegalWordSizeException, PhysicalException;

	int read(int asid, int virtualAddress, int size, boolean isData) throws IllegalWordSizeException, PhysicalException;

	void setCache(Memory cache);
	
	Memory getCache();
}
