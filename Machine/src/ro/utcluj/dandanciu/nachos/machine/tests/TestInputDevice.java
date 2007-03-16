package ro.utcluj.dandanciu.nachos.machine.tests;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import ro.utcluj.dandanciu.nachos.machine.InputDevice;

public class TestInputDevice extends TestCase {
	
	InputDevice id;
	
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(TestInputDevice.class);

	public void setUp() {
		if (logger.isDebugEnabled()) {
			logger.debug("setUp() - start"); //$NON-NLS-1$
		}

		PropertyConfigurator.configure("log4j.properties");
		DummyApic da = new DummyApic();
		id = new InputDevice( da, 0 );
		da.init(id);

		if (logger.isDebugEnabled()) {
			logger.debug("setUp() - end"); //$NON-NLS-1$
		}
	}
	
	public void testID(){
		if (logger.isDebugEnabled()) {
			logger.debug("testID() - start"); //$NON-NLS-1$
		}
		
		id.addChar('A');
		id.addChar('N');
		id.addChar('I');
		
		System.out.println(id.getChar());
		System.out.println(id.getChar());
		System.out.println(id.getChar());
		
		if (logger.isDebugEnabled()) {
			logger.debug("testID() - end"); //$NON-NLS-1$
		}
	}
}
