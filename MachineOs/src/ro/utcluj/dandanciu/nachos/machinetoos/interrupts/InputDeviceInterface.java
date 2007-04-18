package ro.utcluj.dandanciu.nachos.machinetoos.interrupts;

import ro.utcluj.dandanciu.nachos.machinetoos.Interruptable;

public interface InputDeviceInterface extends Interruptable{

	char getChar();

}