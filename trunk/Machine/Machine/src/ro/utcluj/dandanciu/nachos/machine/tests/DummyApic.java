package ro.utcluj.dandanciu.nachos.machine.tests;

import ro.utcluj.dandanciu.nachos.machine.Apic;
import ro.utcluj.dandanciu.nachos.machine.Device;
import ro.utcluj.dandanciu.nachos.machine.Interrupt;

public class DummyApic implements Apic {
	
	public void init(Device device) {
		Interrupt i = new Interrupt();
		i.setDevice(device);
		i.setPriority(0);
		this.interrupts = new Interrupt[1];
		this.interrupts[0] = i;
	}

	private Interrupt[] interrupts;

	public void IRqX(int code) {
		this.interrupts[code].getDevice().handleInterrupt();

	}

	public void setInterruptsArray(Interrupt[] interrupts) {
		this.interrupts = interrupts;

	}

}
