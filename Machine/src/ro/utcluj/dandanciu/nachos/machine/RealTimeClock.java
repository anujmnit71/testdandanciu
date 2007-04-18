package ro.utcluj.dandanciu.nachos.machine;

import org.apache.log4j.Logger;

import ro.utcluj.dandanciu.nachos.common.IrqType;

import java.util.Timer;
import java.util.TimerTask;

/**
 * CMOS RTC (Real Time Clock) The CMOS RTC is part of the battery-backed memory
 * device that keeps a PC's BIOS settings stable while the PC is powered off.
 * The name CMOS comes from the low-power integrated circuit technology in which
 * this device was originally implemented. There are two main timerelated
 * features in the RTC. <br> 
 * First, there is a continuously running time of day (TOD)
 * clock that keeps time in year/month/day hour:minute:second format. This clock
 * can be read only to the nearest second. There is also a timer that can
 * generate periodic interrupts at any power-of-two rate from 2Hz to 8192Hz.
 * This timer fits the block diagram model in Figure 1, with the restriction
 * that the counter cannot be read or written, and the counter input can be set
 * only to a power of two. <br>
 * Multiprocessor and ACPI-capable versions of Microsoft
 * Windows use the CMOS periodic timer as the main system timer. Two other
 * interrupts can also be enabled: the update interrupt and the alarm interrupt.
 * The update interrupt occurs once per second. It is supposed to signal the TOD
 * clock turning over to the next second. The alarm interrupt occurs when the
 * time of day matches a specified value or pattern.
 * 
 * @author Dan Danciu
 * 
 */
public class RealTimeClock extends Device{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(RealTimeClock.class);
	
	private double frequency = 15.625;
	private int timesRTC = (int) (1000 / frequency);
	
	private long seconds;
	
	private final class RealTimeClockTask extends TimerTask {

		@Override
		public void run() {
			if (logger.isDebugEnabled()) {
				logger.debug("run() - start"); //$NON-NLS-1$
			}

			RealTimeClock.this.handle();
			
			if (logger.isDebugEnabled()) {
				logger.debug("run() - end"); //$NON-NLS-1$
			}
		}
		
	}
	
	@Override
	public void handle() {
		if (logger.isDebugEnabled()) {
			logger.debug("handle() - start"); //$NON-NLS-1$
		}

		timesRTC--;
		if(timesRTC  == 0){
			seconds++;
			timesRTC = (int) (1000 / frequency);	
		}
		this.apic.IRqX(this.code);

		if (logger.isDebugEnabled()) {
			logger.debug("handle() - end"); //$NON-NLS-1$
		}
	}
	
	public RealTimeClock(Apic apic) {
		super(apic, IrqType.RealTimeClock.ordinal());
		
		long delay = 0;   // delay
	    long period = (long) frequency;  // repeat
	    Timer timer = new Timer();
	    
	    timer.scheduleAtFixedRate(new RealTimeClockTask(), delay, period);
	    
	    seconds = System.currentTimeMillis() / 1000;
	}

	/**
	 * @return the frequency
	 */
	public double getFrequency() {
		return frequency;
	}

	/**
	 * @param frequency the frequency to set
	 */
	public void setFrequency(double frequency) {
		this.frequency = frequency;
	}

	/**
	 * @return the seconds
	 */
	public long getSeconds() {
		return seconds;
	}

	/**
	 * @param seconds the seconds to set
	 */
	public void setSeconds(long seconds) {
		this.seconds = seconds;
	}
}
