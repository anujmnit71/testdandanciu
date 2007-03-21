package ro.utcluj.dandanciu.nachos.machine.vm;

import org.apache.log4j.Logger;

import ro.utcluj.dandanciu.nachos.machine.Memory;
import ro.utcluj.dandanciu.nachos.machine.PhysicalException;
import ro.utcluj.dandanciu.nachos.machine.Word;
import ro.utcluj.dandanciu.nachos.machine.exceptions.IllegalWordSizeException;
import ro.utcluj.dandanciu.nachos.machine.vm.exceptions.EntryNotFoundException;
import ro.utcluj.dandanciu.nachos.machine.vm.exceptions.IllegalMemoryAccessException;
import ro.utcluj.dandanciu.nachos.machine.vm.exceptions.NotCachableException;
import ro.utcluj.dandanciu.nachos.machine.vm.exceptions.TlbDirtyException;
import ro.utcluj.dandanciu.nachos.machine.vm.exceptions.TlbInvalidException;

public class LocalMmu extends AbstractMmu {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(LocalMmu.class);

	private final static int TLB_SIZE = 3;

	private final static int CACHE_ADDRESS_WORD_SIZE = 2;

	private final static int CACHAE_DATA_WORD_SIZE = 4;

	private final static int CACHE_SIZE_IN_KB = 24;

	/**
	 * Data TLB
	 */
	MiniTlb dtlb = new MiniTlb(TLB_SIZE);

	/**
	 * Instruction TLB
	 */
	MiniTlb itlb = new MiniTlb(TLB_SIZE);
	
	GlobalMmu globalMmu;

	Memory cache = new Memory(CACHE_ADDRESS_WORD_SIZE, CACHAE_DATA_WORD_SIZE,
			CACHE_SIZE_IN_KB);

	public int readMemory(int asid, int virtualAddress, int size) {
		assert (false);
		return 0;
	}

	public void writeMemory(int asid, int virtualAddress, int value, int size) {
		assert (false);
	}

	public int readData(int asid, int virtualAddress, int size)
			throws IllegalWordSizeException, PhysicalException {
		int realAddress = 0;

		try {
			realAddress = dtlb.translate(virtualAddress, asid, false);
		} catch (EntryNotFoundException e) {
			logger.warn(e);
			globalMmu.refresh(this, virtualAddress);
		} catch (IllegalMemoryAccessException e) {
			logger.warn(e);
			throw PhysicalException.ADDRESS_ERROR_EXCEPTION;
		} catch (TlbDirtyException e) {
			logger.warn(e);
			globalMmu.refresh(this, virtualAddress);
			return readData(asid, virtualAddress, size);
		} catch (TlbInvalidException e) {
			logger.warn(e);
			throw PhysicalException.ADDRESS_ERROR_EXCEPTION;
		} catch (NotCachableException e) {
			logger.warn(e);
			return globalMmu.readMemory(asid, virtualAddress, size);
		}

		Word address = Word.getWordOfSize(CACHE_ADDRESS_WORD_SIZE);
		address.setValue(realAddress);
		Word value = Word.getWordOfSize(size);
		cache.load(address, value);

		return value.intValue();

	}

	public void setDirty(int virtualAddress) {
		dtlb.setDirty(virtualAddress);
		itlb.setDirty(virtualAddress);		
	}

}
