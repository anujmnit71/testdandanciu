/**
 * 
 */
package ro.utcluj.dandanciu.nachos.machine;

import ro.utcluj.dandanciu.nachos.machine.exceptions.IllegalAddressWordSizeException;
import ro.utcluj.dandanciu.nachos.machine.exceptions.IllegalAddressingWordSizeException;

/**
 * @author Dan Danciu
 * 
 */
public class Memory {

	/**
	 * The size of the address word, number of bytes
	 */
	private int addressWordSize;

	/**
	 * Holds the data in this memory
	 */
	private Byte[] data;

	private int addressingMask;

	/**
	 * 
	 * @param addressWordSize
	 * @param dataWordSize
	 * @param capacity
	 *            the size of the memory in KBytes
	 * @throws IllegalAddressingWordSizeException
	 */
	public Memory(int addressWordSize, int dataWordSize, double capacity)
			throws IllegalAddressingWordSizeException {

		int bitsNeededForAddressing = 10 + ((int) (Math.log10(capacity) / Math
				.log10(2)));
		int bytesNeededForAddressing = bitsNeededForAddressing / 8;

		if ((bitsNeededForAddressing % 8) != 0)
			bytesNeededForAddressing += 1;

		if (bytesNeededForAddressing > addressWordSize) {
			throw new IllegalAddressingWordSizeException();
		}

		addressingMask = 0;

		for (int i = 0; i < bitsNeededForAddressing; i++) {
			addressingMask += Math.pow(2, i);
		}

		this.addressWordSize = addressWordSize;
		data = new Byte[addressingMask];
	}

	public void load(Word address, Word value)
			throws IllegalAddressWordSizeException {
		if (address.getSize() != addressWordSize) {
			throw new IllegalAddressWordSizeException();
		}

		for (int i = 0; i < value.getSize(); i++) {
			value.setByte(i, data[address.intValue(addressingMask) + i]);
		}
	}

	public void store(Word address, Word value)
			throws IllegalAddressWordSizeException {
		if (address.getSize() != addressWordSize) {
			throw new IllegalAddressWordSizeException();
		}

		for (int i = 0; i < value.getSize(); i++) {
			this.data[address.intValue(addressingMask) + i] = value.getData()[i];
		}

	}

	public void clean() {
		for (int i = 0; i < data.length; i++) {
			data[i] = 0;
		}
	}

	/**
	 * @return the addressWordSize
	 */
	public int getAddressWordSize() {
		return addressWordSize;
	}

	public void copy(Memory target, int targetAddress, int address, int size) {
		for(int i = 0; i < size; i++){
			target.data[targetAddress + i] = data[address + i];
		}
	}

}
