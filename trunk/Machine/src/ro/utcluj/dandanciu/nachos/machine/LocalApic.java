package ro.utcluj.dandanciu.nachos.machine;

import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.TreeSet;

import ro.utcluj.dandanciu.nachos.common.IrqType;
import ro.utcluj.dandanciu.nachos.machine.vm.InterruptRequest;

public class LocalApic implements Apic{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(LocalApic.class);
	
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

	public synchronized InterruptRequest getNext() {
		if (logger.isDebugEnabled()) {
			logger.debug("getNext() - start"); //$NON-NLS-1$
		}

		Iterator<InterruptRequest> it = data.iterator();

		while (it.hasNext()) {
			InterruptRequest ir = it.next();
			if (ir.getInterrupt().getDevice() != null
					&& ir.getInterrupt().getDevice().isInService())
				continue;

			if (logger.isDebugEnabled()) {
				logger.debug("getNext() - end"); //$NON-NLS-1$
			}
			return ir;

		}
		logger.warn("LocalApic.getNext() returns null");
		if (logger.isDebugEnabled()) {
			logger.debug("getNext() - end"); //$NON-NLS-1$
		}
		return null;
	}

	public synchronized void IRqX(int code) {
		Interrupt intr = Machine.getInstance().interruptTable.get(IrqType.get(code));
		InterruptRequest ir = new InterruptRequest();
		ir.setInterrupt(intr);
		ir.setStatus(InterruptRequest.PENDING);
		data.add(ir);
		logger.debug("INTERUPT QUEUE: size = " + data.size());
	}

	public synchronized boolean isEmpty() {
		return data.isEmpty();
	}
	
	
	
	
	
}