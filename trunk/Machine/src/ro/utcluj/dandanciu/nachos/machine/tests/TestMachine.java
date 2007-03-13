package ro.utcluj.dandanciu.nachos.machine.tests;

import junit.framework.TestCase;

import org.apache.log4j.PropertyConfigurator;

import ro.utcluj.dandanciu.nachos.machine.Machine;

public class TestMachine extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
		PropertyConfigurator.configure("log4j.properties");

	}
	
	public void testMachine(){
		Machine.getInstance().start();
	}

}
