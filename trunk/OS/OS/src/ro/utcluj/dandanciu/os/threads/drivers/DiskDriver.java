package ro.utcluj.dandanciu.os.threads.drivers;

import ro.utcluj.dandanciu.nachos.machinetoos.Interruptable;
import ro.utcluj.dandanciu.nachos.ostomachine.InterruptServiceRoutineInterface;

public class DiskDriver {

	
	public InterruptServiceRoutineInterface getReadSectorISR() {
		return new InterruptServiceRoutineInterface() {
			public void doHandle(Interruptable interrupt) {
				// TODO Auto-generated method stub
				
			}
			
		};
	}
	
	public InterruptServiceRoutineInterface getWriteSectorISR() {
		return new InterruptServiceRoutineInterface() {
			public void doHandle(Interruptable interrupt) {
				// TODO Auto-generated method stub
				
			}
			
		};
	}
}
