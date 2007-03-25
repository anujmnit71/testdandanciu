package ro.utcluj.dandanciu.nachos.machine;

import ro.utcluj.dandanciu.nachos.machine.exceptions.IllegalWordSizeException;

/**
 * Holds the implementation for a bit word of 1-4 Bytes
 * 
 * @author Dan Danciu
 * 
 */
public class Word {

	/**
	 * Specifies the size of the word in Bytes It is read only.
	 */
	private final int size;

	/**
	 * Holds the data in the word
	 */
	private Byte[] data = null;

	/**
	 * Constructs a word of a soecified size;
	 * 
	 * Legal values are 1, 2 and 4, although it is not checked
	 * 
	 * @param size
	 */
	protected Word(int size) {
		this.size = size;
		data = new Byte[size];
	}

	/**
	 * @return the data
	 */
	public Byte[] getData() {
		return data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(Byte[] data) {
		this.data = data;
	}

	/**
	 * @return the size
	 */
	public int getSize() {
		return size;
	}

	public void setByte(int i, Byte v) {
		data[i] = v;
	}

	public int intValue() {
		int result = 0;
		for (int i = size - 1; i >= 0; i--) {
			result |= (data[i] & 0xFF) << (i * 8);
		}

		return result;
	}

	public int intValue(int mask) {
		return intValue() & mask;
	}

	public void setValue(int value) {
		for (int i = size - 1; i >= 0; i--) {
			data[i] = new Byte(new Integer((value >> (i * 8)) & 0xFF)
					.byteValue());
		}
	}

	public boolean[] getBits(int index, int lenght) {
		boolean[] rez = new boolean[lenght];
		String binary = Integer.toBinaryString(intValue());
		for (int i = 0; i < lenght; i++) {
			rez[i] = (binary.charAt((size * 8) - index + i) == '1');
		}
		return rez;
	}

	public int getBitsIntValue(int index, int lenght) {
		String binary = Integer.toBinaryString(intValue()).substring(
				(size * 8) - index - 1, lenght);
		return Integer.parseInt(binary, 2);
	}

	public final static Word getByteWord() {
		return new Word(1);
	}

	public final static Word getDoubleWord() {
		return new Word(2);
	}

	public final static Word getQuadWord() {
		return new Word(4);
	}

	public final static Word getWordOfSize(int size)
			throws IllegalWordSizeException {
		switch (size) {
		case 1:
			return getByteWord();
		case 2:
			return getDoubleWord();
		case 4:
			return getQuadWord();

		default:
			throw new IllegalWordSizeException();
		}
	}

	public final static int getMaxWord(int size) {
		Word word = new Word(size);
		for (int i = 0; i < size; i++) {
			word.data[i] = new Byte((byte) 0xFF);
		}
		return word.intValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return this.intValue() == ((Word) obj).intValue();
	}

	public String toString() {
		StringBuffer buff = new StringBuffer();

		buff.append("[");
		for (int i = size; i > 0; i--) {
			buff.append(Integer.toHexString(data[i-1]));
			buff.append(" ");
		}
		buff.append("] : ");
		buff.append(intValue());
		return buff.toString();
	}
	
	public static String toBinary(int value, int size){
		String binary = Integer.toBinaryString(value);
		for(int i = binary.length(); i < size; i++){
			binary = "0" + binary;
		}
		return binary;
		
	}

}
