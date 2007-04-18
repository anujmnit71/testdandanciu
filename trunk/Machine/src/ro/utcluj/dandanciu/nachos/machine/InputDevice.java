package ro.utcluj.dandanciu.nachos.machine;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ro.utcluj.dandanciu.nachos.machinetoos.interrupts.InputDeviceInterface;

public class InputDevice extends Device implements InputDeviceInterface {

	public InputDevice(Apic apic, int code) {
		super(apic, code);
	}

	private Queue<Character> data = new LinkedList<Character>();

	private char buffer;

	Lock lock = new ReentrantLock();

	Condition condition = lock.newCondition();

	boolean empty = true;

	/* (non-Javadoc)
	 * @see ro.utcluj.dandanciu.nachos.machine.InputDeviceInterface#getChar()
	 */
	public char getChar() {
		return data.poll();
	}

	public void addChar(char c) {
		lock.lock();
		try {
			while (!empty)
				condition.await();
			this.buffer = c;
			empty = false;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		lock.unlock();
		triggerInterrupt();
	}

	public void handle() {

		lock.lock();
		if (!empty) {
			this.data.offer(buffer);
			empty = true;
			condition.signal();
		}
		lock.unlock();

	}
}
