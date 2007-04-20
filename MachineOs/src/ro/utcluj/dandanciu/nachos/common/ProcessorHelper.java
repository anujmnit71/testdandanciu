package ro.utcluj.dandanciu.nachos.common;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import ro.utcluj.dandanciu.nachos.machinetoos.Interruptable;
import ro.utcluj.dandanciu.nachos.machinetoos.ProcessorInterface;
import ro.utcluj.dandanciu.nachos.ostomachine.InterruptServiceRoutineInterface;
import ro.utcluj.dandanciu.nachos.ostomachine.InterruptServiceTable;
import ro.utcluj.dandanciu.nachos.ostomachine.ThreadContextHelper;

//TODO: make interfaces for ->MACHINE and for ->OS
public class ProcessorHelper {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(ProcessorHelper.class);

	private static final Set<ProcessorHelper> processorHelpers = new HashSet<ProcessorHelper>(ConfigOptions.NoOfProcs);
	
	public static ProcessorHelper generate(ProcessorInterface processor) {
		ProcessorHelper newHelper = new ProcessorHelper();
		newHelper.processor = processor;
		processorHelpers.add(newHelper);
		return newHelper;
	}
	
	public static ProcessorHelper getAvailableProcessorHelper(){
		for(ProcessorHelper helper : processorHelpers) {
			if(helper.current == null)
				return helper;
		}
		//this normally shouldn't happend
		logger.warn("ProcessorHelper.getAvailableProcessorHelper() - returns null");
		return null;
	}

	private final int id;
	
	private ProcessorInterface processor;
		
	private ThreadContextHelper<Runnable> current;

	private ProcessorHelper(){
		this.id = processorHelpers.size();
		current = null;
	}

	/**
	 * @return the current
	 */
	public ThreadContextHelper<Runnable> getCurrent() {
		return current;
	}

	/**
	 * @param current the current to set
	 */
	public void setCurrent(ThreadContextHelper<Runnable> current) {
		this.current = current;
	}
	
	/**
	 * 
	 * @param code
	 * @param device can be null for exceptions
	 */
	public void interrupt(InterruptCode code, Interruptable device){
		if (logger.isDebugEnabled()) {
			logger.debug("interrupt(InterruptCode, Interruptable) - start"); //$NON-NLS-1$
		}
		
		InterruptServiceRoutineInterface isr = InterruptServiceTable.getServiceByCode(code);
		if(isr != null) {		//not all interrupt must have ISRs
			isr.doHandle(device);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("interrupt(InterruptCode, Interruptable) - end"); //$NON-NLS-1$
		}
	}
	
	public void use(ThreadContextHelper<Runnable> tch){
		logger.info("USE  --- Proc#"+id);
		this.current = tch;
		this.processor.bussy();
	}
	
	public void idle() {
		logger.info("IDLE  --- Proc#"+id);
		this.current = null;
		this.processor.idle();
	}
}
