package ro.utcluj.dandanciu.nachos.machine;

import java.util.Iterator;
import java.util.TreeSet;

public class LocalApic {
	
	private int id = -1;
	
	private TreeSet<InterruptRequest> data = new TreeSet<InterruptRequest>();
	
	public LocalApic(int id){
		this.id = id;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	public void addRequest(InterruptRequest ir) {
		data.add(ir);
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
	
	
	
	
	
}