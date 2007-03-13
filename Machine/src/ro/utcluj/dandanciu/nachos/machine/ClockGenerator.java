package ro.utcluj.dandanciu.nachos.machine;

import org.apache.log4j.Logger;

import java.util.Timer;
import java.util.TimerTask;

import ro.utcluj.dandanciu.nachos.machine.utils.ConfigOptions;

public class ClockGenerator extends Timer {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(ClockGenerator.class);

	private class ClockGeneratorTask extends TimerTask {
		
		@Override
		public void run() {
			if (logger.isDebugEnabled()) {
				logger.debug("ClockGeneratorTask.run() - start"); //$NON-NLS-1$
			}

			Machine.getInstance().tick();
			
			//reschedule
			schedule();

			if (logger.isDebugEnabled()) {
				logger.debug("ClockGeneratorTask.run() - end"); //$NON-NLS-1$
			}
		}
		void schedule() {
			ClockGenerator.this.schedule(new ClockGeneratorTask(), ConfigOptions.TickLenght);
		}

	}

	public ClockGenerator() {
		super();
		if (logger.isDebugEnabled()) {
			logger.debug("ClockGenerator() - start"); //$NON-NLS-1$
		}

		new ClockGeneratorTask().schedule();

		if (logger.isDebugEnabled()) {
			logger.debug("ClockGenerator() - end"); //$NON-NLS-1$
		}
	}


}
