package ro.utcluj.dandanciu.nachos.machine.tests;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import ro.utcluj.dandanciu.nachos.machine.InputDevice;
import ro.utcluj.dandanciu.nachos.machine.OutputDevice;

public class TestOutputDevice extends TestCase {
	
	OutputDevice od;
	
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(TestOutputDevice.class);

	public void setUp() {
		if (logger.isDebugEnabled()) {
			logger.debug("setUp() - start"); //$NON-NLS-1$
		}

		PropertyConfigurator.configure("log4j.properties");
		DummyApic da = new DummyApic();
		od = new OutputDevice( System.out, da, 0 );
		da.init(od);

		if (logger.isDebugEnabled()) {
			logger.debug("setUp() - end"); //$NON-NLS-1$
		}
	}
	
	public void testID(){
		if (logger.isDebugEnabled()) {
			logger.debug("testID() - start"); //$NON-NLS-1$
		}
		
		od.addChar('A');
		od.addChar('N');
		od.addChar('I');
		
		od.outChar();
		od.outChar();
		od.outChar();
		
		if (logger.isDebugEnabled()) {
			logger.debug("testID() - end"); //$NON-NLS-1$
		}
	}
}
