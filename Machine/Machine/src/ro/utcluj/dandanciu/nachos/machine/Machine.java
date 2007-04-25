package ro.utcluj.dandanciu.nachos.machine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.PropertyConfigurator;

import ro.utcluj.dandanciu.nachos.common.ConfigOptions;
import ro.utcluj.dandanciu.nachos.common.InterruptCode;
import ro.utcluj.dandanciu.nachos.common.IrqType;
import ro.utcluj.dandanciu.nachos.machine.vm.GlobalMmu;
import ro.utcluj.dandanciu.nachos.machine.vm.MemoryConfigOptions;
import ro.utcluj.dandanciu.nachos.machinetoos.MemoryManagementUnit;

public class Machine {

	private ClockGenerator clockGenerator;

	private static List<Processor> processors;

	private Memory mainMemory;

	private IOApic ioApic;

	private GlobalMmu mmu;

	private RealTimeClock rtc;

	private static Machine machine;
	
	public Map<IrqType, Interrupt> interruptTable = new HashMap<IrqType, Interrupt>();

	/**
	 * Initialize all the components of the machine
	 */
	private Machine() {
		
		ioApic = new IOApic();
		
		// initialize the processors
		processors = new ArrayList<Processor>(ConfigOptions.NoOfProcs);
		for (int i = 0; i < ConfigOptions.NoOfProcs; i++) {
			processors.add(new Processor(i));
			ioApic.addLocalApic(processors.get(i).getLocalApic());
		}

		createInterruptTable();
	
		// initialize the main memory
		mainMemory = new Memory(MemoryConfigOptions.MEM_ADDRESS_SIZE,
				MemoryConfigOptions.MEM_DATA_SIZE, MemoryConfigOptions.MEM_SIZE);
		mainMemory.clean();

		mmu = new GlobalMmu(processors, mainMemory);

	}
	
	

	public void start() {
		clockGenerator = new ClockGenerator();
		rtc = new RealTimeClock(ioApic);
	}

	public static Machine getInstance() {
		if (machine == null)
			machine = new Machine();
		return machine;
	}

	public void tick() {
		for (Iterator<Processor> it = processors.iterator(); it.hasNext();) {
			it.next().tick();
		}
	}

	public static void main(String[] args) {
		PropertyConfigurator.configure("log4j.properties");

		Machine.getInstance().start();
	}

	public void reset() {
		// TODO reset the machine

	}
	
	public MemoryManagementUnit getMmu(){
		return mmu;
	}
	
	
	private void createInterruptTable() {
		interruptTable = new HashMap<IrqType, Interrupt>();
		
		interruptTable.put(IrqType.Keyboard, null);
		interruptTable.put(IrqType.InterruptController, null);
		interruptTable.put(IrqType.COM2,null);
		interruptTable.put(IrqType.COM1, null);
		interruptTable.put(IrqType.SoundCard, null);
		interruptTable.put(IrqType.FloppyController, null);
		interruptTable.put(IrqType.PrinterPort, null);
		interruptTable.put(IrqType.RealTimeClock, getRealTimeClockInterrupt());
		interruptTable.put(IrqType.DisplayAdapter, null);
		interruptTable.put(IrqType.EthernetCard, null);
		interruptTable.put(IrqType.PS2Mouse, null);
		interruptTable.put(IrqType.UsbPort, null);
		interruptTable.put(IrqType.IDEControllerHDD, null);
		interruptTable.put(IrqType.IDEControllerCDROM, null);
		
		
	}



	private Interrupt getRealTimeClockInterrupt() {
		Interrupt i = new Interrupt();
		i.setDevice(rtc);
		i.setPriority(0);
		i.setCode(InterruptCode.TICK);
		return i;
	}

}
