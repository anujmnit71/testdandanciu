package ro.utcluj.dandanciu.nachos.machine;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import ro.utcluj.dandanciu.nachos.common.ConfigOptions;

public class ClockGenerator {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(ClockGenerator.class);

	private class ClockGeneratorTask extends TimerTask {
		
		@Override
		public void run() {
			if (logger.isDebugEnabled()) {
				logger.debug("ClockGeneratorTask.run() - start"); 
			}

			Machine.getInstance().tick();
		
			if (logger.isDebugEnabled()) {
				logger.debug("ClockGeneratorTask.run() - end"); 
			}
		}
	}


    
	public ClockGenerator() {
		if (logger.isDebugEnabled()) {
			logger.debug("ClockGenerator() - start"); 
		}

		int delay = ConfigOptions.TickLenght;   // delay
	    int period = ConfigOptions.TickLenght;  // repeat
	    Timer timer = new Timer();
	    
	    timer.scheduleAtFixedRate(new ClockGeneratorTask(), delay, period);

		if (logger.isDebugEnabled()) {
			logger.debug("ClockGenerator() - end"); 
		}
	}


}
