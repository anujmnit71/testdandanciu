package ro.utcluj.dandanciu.nachos.machine.vm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ro.utcluj.dandanciu.nachos.common.ConfigOptions;
import ro.utcluj.dandanciu.nachos.machine.Memory;
import ro.utcluj.dandanciu.nachos.machine.PhysicalException;
import ro.utcluj.dandanciu.nachos.machine.Processor;
import ro.utcluj.dandanciu.nachos.machine.Word;
import ro.utcluj.dandanciu.nachos.machine.exceptions.IllegalWordSizeException;
import ro.utcluj.dandanciu.nachos.machine.vm.exceptions.EntryNotFoundException;
import ro.utcluj.dandanciu.nachos.machine.vm.exceptions.IllegalMemoryAccessException;
import ro.utcluj.dandanciu.nachos.machine.vm.exceptions.NotCachableException;
import ro.utcluj.dandanciu.nachos.machine.vm.exceptions.TlbDirtyException;
import ro.utcluj.dandanciu.nachos.machine.vm.exceptions.TlbInvalidException;
import ro.utcluj.dandanciu.nachos.machinetoos.MemoryManagementUnit;

public class GlobalMmu extends AbstractGlobalMmu implements
		MemoryManagementUnit {

	private JointTlb jtlb = new JointTlb(MemoryConfigOptions.JTLB_SIZE);

	private Memory memory = null;/*
									 * new Memory(MEM_ADDRESS_SIZE,
									 * MEM_DATA_SIZE, MEM_SIZE);
									 */

	private List<AbstractLocalMmu> localMmus;

	public GlobalMmu(List<Processor> procs, Memory memory) {
		this.memory = memory;
		localMmus = new ArrayList<AbstractLocalMmu>(procs.size());
		for (Iterator<Processor> iter = procs.iterator(); iter.hasNext();) {
			Processor proc = iter.next();
			LocalMmu localMmu = new LocalMmu();
			localMmu.setCache(proc.getCache());
			proc.setLocalMmu(localMmu);
			localMmus.add(localMmu);
		}

	}

	public int readMemory(int asid, int virtualAddress, int size)
			throws IllegalWordSizeException, PhysicalException {
		int realAddress = 0;
		try {
			realAddress = jtlb.translate(virtualAddress, asid, false);
		} catch (EntryNotFoundException e) {
			throw PhysicalException.PAGE_FAULT_EXCEPTION;
		} catch (IllegalMemoryAccessException e) {
			throw PhysicalException.ILLEGAL_INSTRUCTION_EXCEPTION;
		} catch (TlbDirtyException e) {
			// TODO refresh local mmu page
		} catch (TlbInvalidException e) {
			throw PhysicalException.ILLEGAL_INSTRUCTION_EXCEPTION;
		} catch (NotCachableException e) {
			// TODO read from memory
		}

		Word address = Word.getWordOfSize(MemoryConfigOptions.MEM_ADDRESS_SIZE);
		address.setValue(realAddress);
		Word value = Word.getWordOfSize(size);
		memory.load(address, value);

		return value.intValue();
	}

	public void writeMemory(AbstractLocalMmu callerMmu, int asid,
			int virtualAddress, int value, int size)
			throws IllegalWordSizeException, PhysicalException {

		int realAddress = 0;
		try {
			realAddress = jtlb.translate(virtualAddress, asid, false);
		} catch (EntryNotFoundException e) {
			throw PhysicalException.PAGE_FAULT_EXCEPTION;
		} catch (IllegalMemoryAccessException e) {
			throw PhysicalException.ILLEGAL_INSTRUCTION_EXCEPTION;
		} catch (TlbDirtyException e) {
			// TODO refresh local mmu page
		} catch (TlbInvalidException e) {
			throw PhysicalException.ILLEGAL_INSTRUCTION_EXCEPTION;
		} catch (NotCachableException e) {
			// TODO read from memory
		}

		Word address = Word.getWordOfSize(MemoryConfigOptions.MEM_ADDRESS_SIZE);
		address.setValue(realAddress);
		Word valueW = Word.getWordOfSize(size);
		valueW.setValue(value);
		memory.store(address, valueW);
		for (Iterator<AbstractLocalMmu> it = localMmus.iterator(); it.hasNext();) {
			AbstractLocalMmu mmu = it.next();
			if (mmu.equals(callerMmu)) {
				continue;
			}
			mmu.setDirty(virtualAddress);
		}
	}

	public void refresh(Memory cache, int cahceFrame, int virtualAddress) {
		int memoryFrame = jtlb.getRealPfn(virtualAddress);
		memory.copy(cache, cahceFrame * ConfigOptions.VMPageSize, memoryFrame
				* ConfigOptions.VMPageSize, ConfigOptions.VMPageSize);

	}

	@Override
	public void tlbWriteIndex(Word entryWord, int index) {
		jtlb.store(entryWord, index);
	}

	@Override
	public void tlbWriteRandom(Word entryWord) {
		int index = (int) ((Math.random() * 100) % jtlb.size);
		tlbWriteIndex(entryWord, index);
	}

	@Override
	public Word tlbRead(int index) {
		return jtlb.get(index);
	}

	@Override
	public Word tlbProbe() {
		int index = (int) ((Math.random() * 100) % jtlb.size);
		return tlbRead(index);
	}

}
