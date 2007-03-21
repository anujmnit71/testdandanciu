package ro.utcluj.dandanciu.nachos.machine.vm;

import ro.utcluj.dandanciu.nachos.machine.Word;

public class JointTlbEntry extends TlbEntry {

	/**
	 * Size: 12 bits
	 */
	private int mask = 0;

	/**
	 * Size: 19
	 */
	private int vpn = -1;

	/**
	 * Size: 1
	 */
	private boolean global = false;

	/**
	 * Size: 8
	 */
	private byte asid = 0;

	/**
	 * Size: 20
	 */
	private int pfn1 = -1;

	/**
	 * Size: 20
	 */
	private int pfn2 = -1;

	/**
	 * Size: 1
	 */
	private boolean cachable1 = true;

	/**
	 * Size: 1
	 */
	private boolean cachable2 = true;

	/**
	 * Size: 1
	 */
	private boolean dirty1 = true;

	/**
	 * Size: 1
	 */
	private boolean dirty2 = true;

	/**
	 * Size: 1
	 */
	private boolean valid1 = false;

	/**
	 * Size: 1
	 */
	private boolean valid2 = false;
	
	@Override
	void load(Word data) {
		
	}

	/**
	 * @return the asid
	 */
	public byte getAsid() {
		return asid;
	}

	/**
	 * @return the cachable1
	 */
	public boolean isCachable1() {
		return cachable1;
	}

	/**
	 * @return the cachable2
	 */
	public boolean isCachable2() {
		return cachable2;
	}

	/**
	 * @return the dirty1
	 */
	public boolean isDirty1() {
		return dirty1;
	}

	/**
	 * @return the dirty2
	 */
	public boolean isDirty2() {
		return dirty2;
	}

	/**
	 * @return the global
	 */
	public boolean isGlobal() {
		return global;
	}

	/**
	 * @return the mask
	 */
	public int getMask() {
		return mask;
	}

	/**
	 * @return the pfn1
	 */
	public int getPfn1() {
		return pfn1;
	}

	/**
	 * @return the pfn2
	 */
	public int getPfn2() {
		return pfn2;
	}

	/**
	 * @return the valid1
	 */
	public boolean isValid1() {
		return valid1;
	}

	/**
	 * @return the valid2
	 */
	public boolean isValid2() {
		return valid2;
	}

	/**
	 * @return the vpn
	 */
	public int getVpn() {
		return vpn;
	}

}
