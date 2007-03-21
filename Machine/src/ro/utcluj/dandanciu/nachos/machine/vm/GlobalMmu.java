package ro.utcluj.dandanciu.nachos.machine.vm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

public class GlobalMmu extends AbstractMmu {

	private static final int JTLB_SIZE = 16;

	private static final int MEM_SIZE = 128;

	private static final int MEM_DATA_SIZE = 4;

	private static final int MEM_ADDRESS_SIZE = 4;
	
	private JointTlb jtlb = new JointTlb(JTLB_SIZE);

	private Memory memory = new Memory(MEM_ADDRESS_SIZE, MEM_DATA_SIZE, MEM_SIZE);

	private List<LocalMmu> localMmus;
	
	
	public GlobalMmu(List<Processor> procs) {
		localMmus = new ArrayList<LocalMmu>(procs.size());
		for(Iterator<Processor> iter = procs.iterator(); iter.hasNext();) {
			Processor proc = iter.next();
			LocalMmu localMmu = new LocalMmu();
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

		Word address = Word.getWordOfSize(MEM_ADDRESS_SIZE);
		address.setValue(realAddress);
		Word value = Word.getWordOfSize(size);
		memory.load(address, value);

		return value.intValue();
	}

	public void writeMemory(LocalMmu callerMmu, int asid, int virtualAddress, int value, int size) {
		//TODO: write
		for(Iterator<LocalMmu> it = localMmus.iterator(); it.hasNext();) {
			LocalMmu mmu = it.next();
			if(mmu.equals(callerMmu)) {
				continue;
			}
			mmu.setDirty(virtualAddress);
		}
	}


	public void refresh(LocalMmu target, int virtualAddress) {
		// TODO create body for refresh
		int pfn = jtlb.getPfn(virtualAddress);
	}

}
