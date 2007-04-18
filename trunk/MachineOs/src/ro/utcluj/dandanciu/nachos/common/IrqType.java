package ro.utcluj.dandanciu.nachos.common;

public enum IrqType {
	Keyboard,
	InterruptController,
	COM2,
	COM1,
	SoundCard,
	FloppyController,
	PrinterPort,
	RealTimeClock,
	DisplayAdapter,
	EthernetCard,
	PS2Mouse,
	UsbPort,
	IDEControllerHDD,
	IDEControllerCDROM;
	
	public static IrqType get(int i){
		return values()[i];
	}
}
