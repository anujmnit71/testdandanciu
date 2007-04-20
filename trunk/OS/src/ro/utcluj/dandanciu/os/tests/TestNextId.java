package ro.utcluj.dandanciu.os.tests;

import java.util.Arrays;

import junit.framework.TestCase;
import ro.utcluj.dandanciu.os.utils.PersistentTable;
import ro.utcluj.dandanciu.os.utils.Utils;

public class TestNextId extends TestCase {
	
	private static final class X {
		public int value;
		
		public String toString(){
			return String.valueOf(value);
		}
	}
	
	private PersistentTable<X> x = new PersistentTable<X>(2,2);
	
	public void setUp(){
		
		for(int i = 0; i < 5; i++) {
			X y = new X();
			y.value = i;
			x.put(y); 
		}
		System.out.println(x);
		x.clean(3);
		System.out.println(x);
		
	}
	
	public void testNextId() {
		assertEquals(x.size(), 6);
	}

}
