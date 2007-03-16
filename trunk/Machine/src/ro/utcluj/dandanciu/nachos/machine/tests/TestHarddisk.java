package ro.utcluj.dandanciu.nachos.machine.tests;

import org.apache.log4j.Logger;

import org.apache.log4j.PropertyConfigurator;

import ro.utcluj.dandanciu.nachos.machine.HardDisk;
import junit.framework.TestCase;

public class TestHarddisk extends TestCase {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(TestHarddisk.class);
	
	public void setUp(){
		if (logger.isDebugEnabled()) {
			logger.debug("setUp() - start"); //$NON-NLS-1$
		}

		PropertyConfigurator.configure("log4j.properties");

		if (logger.isDebugEnabled()) {
			logger.debug("setUp() - end"); //$NON-NLS-1$
		}
	}
	
	public void testCreate() {
		if (logger.isDebugEnabled()) {
			logger.debug("testCreate() - start"); //$NON-NLS-1$
		}

		HardDisk disk = new HardDisk("testHard.data", null, -1);
		
		String testString = "Ana are mere.";
		disk.setBuffer(testString.getBytes());
		disk.writeRequest(0, 0);
		
		disk.readRequest(0, 0);
		
		String newString = new String(disk.getBuffer()).trim();
		
		logger.debug("newString: " + newString);
		
		assertEquals(testString, newString);

		if (logger.isDebugEnabled()) {
			logger.debug("testCreate() - end"); //$NON-NLS-1$
		}
	}

}
