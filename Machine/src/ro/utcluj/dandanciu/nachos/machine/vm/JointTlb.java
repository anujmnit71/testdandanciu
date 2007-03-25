/**
 * 
 */
package ro.utcluj.dandanciu.nachos.machine.vm;

import ro.utcluj.dandanciu.nachos.machine.Word;
import ro.utcluj.dandanciu.nachos.machine.vm.exceptions.EntryNotFoundException;
import ro.utcluj.dandanciu.nachos.machine.vm.exceptions.IllegalMemoryAccessException;
import ro.utcluj.dandanciu.nachos.machine.vm.exceptions.NotCachableException;
import ro.utcluj.dandanciu.nachos.machine.vm.exceptions.TlbDirtyException;
import ro.utcluj.dandanciu.nachos.machine.vm.exceptions.TlbInvalidException;

/**
 * @author Dan Danciu
 * 
 */
public class JointTlb extends AbstractTlb {

	JointTlbEntry[] tlb;

	public JointTlb(int size) {
		super(size);
		tlb = new JointTlbEntry[size];
	}

	public void store(Word data, int position) {
		tlb[position].load(data);
	}
	
	public Word get(int position){
		return tlb[position].getWord();
	}
	
	public int translate(int virtualAddress, int asid, boolean writting)
			throws EntryNotFoundException, IllegalMemoryAccessException,
			TlbInvalidException, TlbDirtyException, NotCachableException {
		
		Word virtualAddressWord = Word.getQuadWord();
		virtualAddressWord.setValue(virtualAddress);
		int vpn = virtualAddressWord.getBitsIntValue(13, 20);
		int index = virtualAddressWord.getBitsIntValue(0, 12);
		int entryIndex = -1;
		boolean evenPage = false;
		for (int i = 0; i < size; i++) {
			if ((tlb[i].getVpn() & tlb[i].getMask()) == vpn) {
				entryIndex = i;
				evenPage = virtualAddressWord.getBits(Integer
						.bitCount(12 + tlb[i].getMask()), 1)[0];
				break;
			}
		}
		JointTlbEntry entry = tlb[entryIndex];

		if ((entryIndex < 0)
				|| (!entry.isGlobal() && entry.getAsid() != asid)) {
			throw new EntryNotFoundException();
		}

		if (evenPage) {

			if (!entry.isValid2())
				throw new TlbInvalidException();

			if (entry.isDirty2() && !writting)
				throw new TlbDirtyException();

			if (!entry.isCachable2())
				throw new NotCachableException();

			return Integer.parseInt(Integer.toBinaryString(entry.getPfn2())
					+ Integer.toBinaryString(index), 2);
		}

		if (!entry.isValid1())
			throw new TlbInvalidException();

		if (entry.isDirty1() && !writting)
			throw new TlbDirtyException();

		if (!entry.isCachable1())
			throw new NotCachableException();

		return Integer.parseInt(Integer.toBinaryString(entry.getPfn1())
				+ Integer.toBinaryString(index), 2);

	}

	private int getPfn(Word virtualAddressWord) {
		int vpn = virtualAddressWord.getBitsIntValue(12, 20);
		int entryIndex = -1;
		boolean evenPage = false;
		for (int i = 0; i < size; i++) {
			if ((tlb[i].getVpn() & tlb[i].getMask()) == vpn) {
				entryIndex = i;
				evenPage = virtualAddressWord.getBits(Integer
						.bitCount(12 + tlb[i].getMask()), 1)[0];
				break;
			}
		}
		JointTlbEntry entry = tlb[entryIndex];
		return evenPage ? entry.getPfn2() : entry.getPfn1();
	}
	
	public int getRealPfn(int virtualAddress) {
		Word virtualAddressWord = Word.getQuadWord();
		virtualAddressWord.setValue(virtualAddress);
		int pfn = getPfn(virtualAddressWord); 
		int subPage = virtualAddressWord.getBitsIntValue(12, 20);
		String binary = Integer.toBinaryString(pfn) + 
				Integer.toBinaryString(subPage);
		
		return Integer.parseInt(binary, 2);		
	}

}
