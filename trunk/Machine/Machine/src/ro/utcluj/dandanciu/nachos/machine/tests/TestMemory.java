package ro.utcluj.dandanciu.nachos.machine.tests;

import ro.utcluj.dandanciu.nachos.machine.Memory;
import ro.utcluj.dandanciu.nachos.machine.Word;
import ro.utcluj.dandanciu.nachos.machine.exceptions.IllegalAddressWordSizeException;
import junit.framework.TestCase;

public class TestMemory extends TestCase {

	private Memory mem;
	private Word address;
	private Word value;
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		address = Word.getDoubleWord();
		address.getData()[0] = new Byte((byte) 155);
		address.getData()[1] = new Byte((byte) 155);
		
		value = Word.getQuadWord();
		value.getData()[0] = new Byte((byte) 155);
		value.getData()[1] = new Byte((byte) 155);
		value.getData()[2] = new Byte((byte) 155);
		value.getData()[3] = new Byte((byte) 155);
		
		mem = new Memory(address.getSize(), value.getSize(), 64);
		
	}
	
	public void testStoreLoad(){
		Word readValue = Word.getQuadWord();
		try {
			mem.store(address, value);
			mem.load(address, readValue);
		} catch (IllegalAddressWordSizeException e) {
			e.printStackTrace();
		}
		
		assertEquals(value, readValue);
		
	}
	
	

}
