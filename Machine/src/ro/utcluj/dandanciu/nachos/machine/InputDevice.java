package ro.utcluj.dandanciu.nachos.machine;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ro.utcluj.dandanciu.utils.collections.ArrayStack;
import ro.utcluj.dandanciu.utils.collections.Stack;

public class InputDevice extends Device {

	public InputDevice(Apic apic, int code) {
		super(apic, code);
		// TODO Auto-generated constructor stub
	}

	private Queue<Character> data = new LinkedList<Character>();

	private char buffer;

	Lock lock = new ReentrantLock();

	Condition condition = lock.newCondition();

	boolean empty = true;

	private boolean available = false;

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
			// TODO Auto-generated catch block
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
