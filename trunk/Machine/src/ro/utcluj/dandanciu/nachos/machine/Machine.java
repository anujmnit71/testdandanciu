package ro.utcluj.dandanciu.nachos.machine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.PropertyConfigurator;

import ro.utcluj.dandanciu.nachos.machine.utils.ConfigOptions;

public class Machine {
	private static List<Processor> processors;

	private Memory mainMemory;

	private ClockGenerator clockGenerator;

	private IOApic ioApic;

	private static Machine machine;

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

		// initialize the main memory
		mainMemory = new Memory(4, 4, 128);
		mainMemory.clean();

	}

	public void start() {
		clockGenerator = new ClockGenerator();
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

	public static void reset() {
		// TODO Auto-generated method stub

	}

}