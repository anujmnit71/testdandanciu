package ro.utcluj.dandanciu.nachos.ostomachine;

import ro.utcluj.dandanciu.nachos.common.InterruptCode;

public class InterruptServiceTable {
	
	private static InterruptServiceRoutineInterface[] data = new InterruptServiceRoutineInterface[256];


	public static void setInterruptService(InterruptCode code, InterruptServiceRoutineInterface isr) {
		data[code.ordinal()] = isr;
	}
	
	public static InterruptServiceRoutineInterface getServiceByCode(InterruptCode code) {
		return data[code.ordinal()];
	}
}
