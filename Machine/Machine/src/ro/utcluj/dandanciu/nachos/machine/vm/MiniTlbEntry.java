package ro.utcluj.dandanciu.nachos.machine.vm;

import ro.utcluj.dandanciu.nachos.machine.Word;

public class MiniTlbEntry extends TlbEntry{

	/**
	 * 
	 */
	private final static int size = 4;

	/**
	 * Size: 8 bites
	 */
	private byte asid = 0;

	/**
	 * Size: 1 bit
	 */
	private boolean global = false;

	/**
	 * Size: 20 bites
	 */
	private int vfn = 0;

	/**
	 * Size: 1 bites
	 */
	private boolean cachable = true;

	/**
	 * Size: 1 bit
	 */
	private boolean dirty = false;

	/**
	 * Size 1 bit
	 */
	private boolean valid = false;

	/**
	 * -------------------------------------
	 * |31- 24| 23|22- 3|     2    | 1 | 0 | 
	 * | asid | G | pfn | cachable | D | V |
	 * |   8  | 1 |  20 |     1    | 1 | 1 | 
	 * -------------------------------------
	 * 
	 * @param data
	 * @param position
	 */
	public void load(Word data) {
		assert (data.getSize() == 4);

		asid = (byte) data.getBitsIntValue(31, 8);
		global = (data.getBitsIntValue(23, 1) == 0);
		vfn = data.getBitsIntValue(22, 20);
		cachable = (data.getBitsIntValue(2, 1) == 0);
		dirty = (data.getBitsIntValue(1, 1) == 0);
		valid = (data.getBitsIntValue(0, 1) == 0);
	}
	
	public Word getWord(){
		String binary = "";
		binary = valid ? "1" : "0";
		binary = dirty ? "1" : "0" + binary;
		binary = cachable ? "1" : "0" + binary;
		binary = Word.toBinary(vfn, 20) + binary;
		binary = global ? "1" : "0" + binary;
		binary = Word.toBinary(asid, 8) + binary;
		
		Word word = Word.getQuadWord();
		word .setValue(Integer.parseInt(binary, 2));
		return word;
	}

	/**
	 * @return the size
	 */
	public static int getSize() {
		return size;
	}

	/**
	 * @return the asid
	 */
	public byte getAsid() {
		return asid;
	}

	/**
	 * @return the cachable
	 */
	public boolean isCachable() {
		return cachable;
	}

	/**
	 * @return the dirty
	 */
	public boolean isDirty() {
		return dirty;
	}

	/**
	 * @return the global
	 */
	public boolean isGlobal() {
		return global;
	}

	/**
	 * @return the pfn
	 */
	public int getVfn() {
		return vfn;
	}

	/**
	 * @return the valid
	 */
	public boolean isValid() {
		return valid;
	}

	/**
	 * @param asid the asid to set
	 */
	public void setAsid(byte asid) {
		this.asid = asid;
	}

	/**
	 * @param cachable the cachable to set
	 */
	public void setCachable(boolean cachable) {
		this.cachable = cachable;
	}

	/**
	 * @param dirty the dirty to set
	 */
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	/**
	 * @param global the global to set
	 */
	public void setGlobal(boolean global) {
		this.global = global;
	}

	/**
	 * @param valid the valid to set
	 */
	public void setValid(boolean valid) {
		this.valid = valid;
	}

	/**
	 * @param vfn the vfn to set
	 */
	public void setVfn(int vfn) {
		this.vfn = vfn;
	}



}
