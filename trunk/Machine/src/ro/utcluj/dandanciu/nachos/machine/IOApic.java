/**
 * 
 */
package ro.utcluj.dandanciu.nachos.machine;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dan Danciu
 * 
 */
public class IOApic {

	private int counter = 0;

	private List<LocalApic> localApics = new ArrayList<LocalApic>();

	private Interrupt[] interrupts = new Interrupt[8];

	public void addLocalApic(LocalApic localApic) {
		localApics.add(localApic);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ro.utcluj.dandanciu.nachos.machine.Apic#connect(ro.utcluj.dandanciu.nachos.machine.Device,
	 *      int)
	 */
	public void connect(Interrupt intr, int position) {
		interrupts[position] = intr;
	}

	private void dispach(int code) {
		counter++; // increment counter
		Interrupt intr = interrupts[code];
		LocalApic apicToService = null;
		if(intr.getApicId() < 0) {
			//if no pending requests exist for this interupt get a local apic by "round robin"
			apicToService = localApics.get(counter % localApics.size());
			intr.setApicId(apicToService.getId());
		} else { 
			apicToService = localApics.get(intr.getApicId());
		}
		InterruptRequest ir = new InterruptRequest();
		ir.setInterrupt(intr);
		ir.setStatus(InterruptRequest.PENDING);
		apicToService.addRequest(ir);
		
	}

	public void IRqX(int code) {
		dispach(code);
	}

}
