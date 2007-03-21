package ro.utcluj.dandanciu.nachos.machine;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class OutputDevice extends Device {

	private OutputStream os;

	public OutputDevice(OutputStream os, Apic apic, int code) {
		super(apic, code);
		this.os = os;
	}

	private Queue<Character> data = new LinkedList<Character>();

	private char buffer;

	Lock lock = new ReentrantLock();

	Condition condition = lock.newCondition();

	boolean empty = true;

	public void outChar() {
		lock.lock();
		try {
			while (!empty)
				condition.await();
			buffer = data.poll();
			empty = false;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		lock.unlock();
		triggerInterrupt();
	}

	public void addChar(char c) {
		this.data.offer(c);
	}

	@Override
	public void handle() {
		lock.lock();
		if (!empty) {
			try {
				this.os.write(buffer);
			} catch (IOException e) {
				e.printStackTrace();
			}
			empty = true;
			condition.signal();
		}
		lock.unlock();
	}
}
