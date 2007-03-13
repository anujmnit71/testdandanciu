/**
 * 
 */
package ro.utcluj.dandanciu.nachos.machine;

/**
 * Represents a register.
 * 
 * @author Dan Danciu
 *
 */
public class Register {
	
	/**
	 * The value currently stored in the Register
	 */
	private Word value;

	/**
	 * Constructs a register of 32 bits
	 */
	public Register() {
		value = Word.getQuadWord();		
	}
	
	public int intValue(){
		return getValue().intValue();
	}

	/**
	 * @return the value
	 */
	public Word getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(Word value) {
		this.value = value;
	}

	/**
	 * Sets the value to the coresponding int value
	 * @param value
	 */
	public void setValue(int value) {
		this.value.setValue(value);
	} 
}
