package ro.utcluj.dandanciu.nachos.machine.vm;

import ro.utcluj.dandanciu.nachos.machine.Word;
import ro.utcluj.dandanciu.nachos.machine.vm.exceptions.EntryNotFoundException;
import ro.utcluj.dandanciu.nachos.machine.vm.exceptions.IllegalMemoryAccessException;
import ro.utcluj.dandanciu.nachos.machine.vm.exceptions.NotCachableException;
import ro.utcluj.dandanciu.nachos.machine.vm.exceptions.TlbDirtyException;
import ro.utcluj.dandanciu.nachos.machine.vm.exceptions.TlbInvalidException;
import ro.utcluj.dandanciu.utils.collections.LastRecentlyUsed;

public class MiniTlb extends AbstractTlb {

	MiniTlbEntry[] tlb;
	
	private LastRecentlyUsed lru;

	public MiniTlb(int size) {
		super(size);
		tlb = new MiniTlbEntry[size];
		lru = new LastRecentlyUsed(size);
	}

	public int translate(int virtualAddress, int asid, boolean writting)
			throws EntryNotFoundException, IllegalMemoryAccessException,
			TlbInvalidException, TlbDirtyException, NotCachableException {
		
		Word virtualAddressWord = Word.getQuadWord();
		virtualAddressWord.setValue(virtualAddress);
		int index = virtualAddressWord.getBitsIntValue(0, 12);
		int vpn = virtualAddressWord.getBitsIntValue(12, 20);
		int entryIndex = getEntryFor(vpn);
		MiniTlbEntry entry = tlb[entryIndex];
		lru.use(entryIndex);

		if ((entry == null) || (!entry.isGlobal() && entry.getAsid() != asid)) {
			throw new EntryNotFoundException();
		}

		if (!entry.isValid())
			throw new TlbInvalidException();

		if (entry.isDirty() && !writting)
			throw new TlbDirtyException();

		if (!entry.isCachable())
			throw new NotCachableException();

		return Integer.parseInt(Integer.toBinaryString(entryIndex)
				+ Integer.toBinaryString(index), 2);
	}

	public void store(Word data, int position) {
		tlb[position].load(data);
	}

	public Word get(int position){
		return tlb[position].getWord();
	}

	public void setDirty(int virtualAddress) {
		Word virtualAddressWord = Word.getQuadWord();
		virtualAddressWord.setValue(virtualAddress);
		int entryIndex = getEntryFor(virtualAddress);
		tlb[entryIndex].setDirty(true);
	}
	
	private int getEntryFor(int vpn){
		for (int i = 0; i < size; i++) {
			if (tlb[i].getVfn() == vpn) {
				return i;
			}
		}
		return -1;
	}

	public int getIndexForNew() {

		for(int i = 0; i < size; i++){
			if(!tlb[i].isValid())
				return i;
		}
		for(int i = 0; i < size; i++){
			if(!tlb[i].isDirty())
				return i;
		}
		return lru.getLastRecentlyUsed();
	}

	public int getIndexFor(int virtualAddress) {
		Word virtualAddressWord = Word.getQuadWord();
		virtualAddressWord.setValue(virtualAddress);
		return getEntryFor(virtualAddressWord.getBitsIntValue(12, 20));
	}
}