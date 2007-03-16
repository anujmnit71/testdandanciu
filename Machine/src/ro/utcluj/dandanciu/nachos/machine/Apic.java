package ro.utcluj.dandanciu.nachos.machine;

public interface Apic {

	void IRqX(int code);

	void setInterruptsArray(Interrupt[] interrupts);
}