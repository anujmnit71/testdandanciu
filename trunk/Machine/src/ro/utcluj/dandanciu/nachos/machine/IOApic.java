/**
 * 
 */
package ro.utcluj.dandanciu.nachos.machine;

import java.util.ArrayList;
import java.util.List;

import ro.utcluj.dandanciu.nachos.common.IrqType;

/**
 * @author Dan Danciu
 * 
 */
public class IOApic implements Apic {

	private int counter = 0;

	private List<LocalApic> localApics = new ArrayList<LocalApic>();

	public void addLocalApic(LocalApic localApic) {
		localApics.add(localApic);
	}

	private void dispach(int code) {
		counter++; // increment counter
		Interrupt intr = Machine.getInstance().interruptTable.get(IrqType.get(code));
		assert(intr != null);
		LocalApic apicToService = null;
		if(intr.getApicId() < 0) {
			//if no pending requests exist for this interupt get a local apic by "round robin"
			apicToService = localApics.get(counter % localApics.size());
			intr.setApicId(apicToService.getId());
		} else { 
			apicToService = localApics.get(intr.getApicId());
		}
		
		apicToService.IRqX(code);
		

	}

	/* (non-Javadoc)
	 * @see ro.utcluj.dandanciu.nachos.machine.Apic#IRqX(int)
	 */
	public void IRqX(int code) {
		dispach(code);
	}

}
