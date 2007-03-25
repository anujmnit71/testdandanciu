package ro.utcluj.dandanciu.nachos.machine.tests;

import ro.utcluj.dandanciu.nachos.machine.Word;
import ro.utcluj.dandanciu.nachos.machine.exceptions.IllegalWordSizeException;
import junit.framework.TestCase;

public class TestWord extends TestCase {

	
	public void testIntValue(){
		Word word = Word.getByteWord();
		word.getData()[0] = new Byte((byte) 0xFF);
		assertEquals(255, word.intValue());

		Word word2 = Word.getDoubleWord();
		word2.getData()[0] = new Byte((byte) 0xFF);
		word2.getData()[1] = new Byte((byte) 0xFF);
		assertEquals(0xFFFF, word2.intValue());
		
		Word word3 = Word.getDoubleWord();
		word3.setValue(30);
		assertEquals(30, word3.intValue());
		
		assertEquals(255, 0xFF);
		
		System.out.println(word);
		System.out.println(word2);
		System.out.println(word3);
		
	}
	
	public void testMaxSize() throws IllegalWordSizeException{
		
		System.out.println(Word.getMaxWord(1));
		System.out.println(Word.getMaxWord(2));
		System.out.println(Word.getMaxWord(3));
		Word max = (Word) Word.getWordOfSize(4);
		max.setValue(Integer.MAX_VALUE);
		System.out.println(max);
		System.out.println(Integer.MAX_VALUE);
	}
}

