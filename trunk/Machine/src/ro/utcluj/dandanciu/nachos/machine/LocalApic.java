package ro.utcluj.dandanciu.nachos.machine;

import java.util.Iterator;
import java.util.TreeSet;

public class LocalApic implements Apic{
	
	private int id = -1;
	
	private TreeSet<InterruptRequest> data = new TreeSet<InterruptRequest>();

	private Interrupt[] interrupts;
	
	public LocalApic(int id){
		this.id = id;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	public InterruptRequest getNext() {

		Iterator<InterruptRequest> it = data.iterator();

		while (it.hasNext()) {
			InterruptRequest ir = it.next();
			if (ir.getInterrupt().getDevice() != null
					&& ir.getInterrupt().getDevice().isInService())
				continue;

			return ir;

		}

		return null;
	}

	public void IRqX(int code) {
		Interrupt intr = interrupts[code];
		InterruptRequest ir = new InterruptRequest();
		ir.setInterrupt(intr);
		ir.setStatus(InterruptRequest.PENDING);
		data.add(ir);
	}

	public void setInterruptsArray(Interrupt[] interrupts) {
		this.interrupts = interrupts;
		
	}

	public boolean isEmpty() {
		return data.isEmpty();
	}
	
	
	
	
	
}