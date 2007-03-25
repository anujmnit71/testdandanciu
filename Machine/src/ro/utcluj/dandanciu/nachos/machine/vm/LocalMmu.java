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

public class LocalMmu extends AbstractLocalMmu implements LocalMemoryManagementUnit{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(LocalMmu.class);



	/**
	 * Data TLB
	 */
	private MiniTlb dtlb = new MiniTlb(MemoryConfigOptions.TLB_SIZE);

	/**
	 * Instruction TLB
	 */
	private MiniTlb itlb = new MiniTlb(MemoryConfigOptions.TLB_SIZE);
	
	private GlobalMmu globalMmu;

	private Memory cache = null;

	public int readMemory(int asid, int virtualAddress, int size) {
		assert (false);
		return 0;
	}

	public void writeMemory(int asid, int virtualAddress, int value, int size) {
		assert (false);
	}

	public int read(int asid, int virtualAddress, int size, boolean isData)
			throws IllegalWordSizeException, PhysicalException {
		int realAddress = 0;
		MiniTlb tlb = isData ? dtlb : itlb;

		try {
			realAddress = tlb.translate(virtualAddress, asid, false);
		} catch (EntryNotFoundException e) {
			logger.warn(e);
			globalMmu.refresh(this.cache, tlb.getIndexForNew(),virtualAddress);
		} catch (IllegalMemoryAccessException e) {
			logger.warn(e);
			throw PhysicalException.ADDRESS_ERROR_EXCEPTION;
		} catch (TlbDirtyException e) {
			logger.warn(e);
			globalMmu.refresh(this.cache, tlb.getIndexFor(virtualAddress), virtualAddress);
			return read(asid, virtualAddress, size, isData);
		} catch (TlbInvalidException e) {
			logger.warn(e);
			throw PhysicalException.ADDRESS_ERROR_EXCEPTION;
		} catch (NotCachableException e) {
			logger.warn(e);
			return globalMmu.readMemory(asid, virtualAddress, size);
		}

		Word address = Word.getWordOfSize(MemoryConfigOptions.CACHE_ADDRESS_WORD_SIZE);
		address.setValue(realAddress);
		Word value = Word.getWordOfSize(size);
		cache.load(address, value);

		return value.intValue();

	}

	public void write(int asid, int virtualAddress, int size, int value,
			boolean isData) throws IllegalWordSizeException, PhysicalException {
		int realAddress = 0;
		MiniTlb tlb = isData ? dtlb : itlb;
		try {
			try {
				realAddress = tlb.translate(virtualAddress, asid, false);
			} catch (EntryNotFoundException e) {
				logger.warn(e);
				globalMmu.refresh(this.cache, tlb.getIndexForNew(), virtualAddress);
			} catch (IllegalMemoryAccessException e) {
				logger.warn(e);
				throw PhysicalException.ADDRESS_ERROR_EXCEPTION;
			} catch (TlbDirtyException e) {
				logger.warn(e);
				// we don't care if it is dirty already, we just write some more
			} catch (TlbInvalidException e) {
				logger.warn(e);
				throw PhysicalException.ADDRESS_ERROR_EXCEPTION;
			}

			Word address = Word.getWordOfSize(MemoryConfigOptions.CACHE_ADDRESS_WORD_SIZE);
			address.setValue(realAddress);
			Word valueW = Word.getWordOfSize(size);
			valueW.setValue(value);
			cache.store(address, valueW);
		} catch (NotCachableException e) {
			logger.warn(e);
		} finally {
			//no meter what
			// also write the global memory
			globalMmu.writeMemory(this, asid, virtualAddress, value, size);
		}
	}

	@Override
	public void setDirty(int virtualAddress) {
		dtlb.setDirty(virtualAddress);
		itlb.setDirty(virtualAddress);
	}

	/**
	 * @return the cache
	 */
	public Memory getCache() {
		return cache;
	}

	/**
	 * @param cache the cache to set
	 */
	public void setCache(Memory cache) {
		this.cache = cache;
	}

}
