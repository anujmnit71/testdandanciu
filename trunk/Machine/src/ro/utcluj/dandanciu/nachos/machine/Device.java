/**
 * 
 */
package ro.utcluj.dandanciu.nachos.machine;

import ro.utcluj.dandanciu.nachos.machinetoos.Interruptable;

/**
 * @author Dan Danciu
 * 
 */
public abstract class Device extends Thread implements Interruptable {

	protected int code;

	protected Apic apic;

	protected boolean inService = false;

	public Device(Apic apic, int code) {
		this.apic = apic;
		this.code = code;
	}

	protected void triggerInterrupt() {
		apic.IRqX(code);
	}

	public final void handleInterrupt() {
		new Thread(new Runnable() {
			public void run() {
				Device.this.handle();
			}
		}).start();
		Thread.yield();
	}

	/**
	 * @return the inService
	 */
	public boolean isInService() {
		return inService;
	}

	/**
	 * @param inService
	 *            the inService to set
	 */
	public void setInService(boolean inService) {
		this.inService = inService;
	}
	
	public abstract void handle();

}
