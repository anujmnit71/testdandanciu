package ro.utcluj.dandanciu.nachos.machine.vm;

import ro.utcluj.dandanciu.nachos.machine.PhysicalException;
import ro.utcluj.dandanciu.nachos.machine.exceptions.IllegalWordSizeException;

public interface MemoryManagementUnit {

	int readMemory(int asid, int virtualAddress, int size) throws IllegalWordSizeException, PhysicalException;

//	void writeMemory(int asid, int virtualAddress, int value, int size);

}
