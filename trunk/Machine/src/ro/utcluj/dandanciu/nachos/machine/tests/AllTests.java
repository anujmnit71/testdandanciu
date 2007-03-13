package ro.utcluj.dandanciu.nachos.machine.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for ro.utcluj.dandanciu.nachos.machine.tests");
		//$JUnit-BEGIN$
		suite.addTestSuite(TestWord.class);
		suite.addTestSuite(TestMemory.class);
		//$JUnit-END$
		return suite;
	}

}
