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
public class IOApic implements Apic {

	private int counter = 0;

	private List<LocalApic> localApics = new ArrayList<LocalApic>();

	private Interrupt[] interrupts = new Interrupt[8];

	public void addLocalApic(LocalApic localApic) {
		localApics.add(localApic);
	}


	public void setInterruptsArray(Interrupt[] interrupts) {
		this.interrupts = interrupts;
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
		
		
	}

	/* (non-Javadoc)
	 * @see ro.utcluj.dandanciu.nachos.machine.Apic#IRqX(int)
	 */
	public void IRqX(int code) {
		dispach(code);
	}

}
