package ro.utcluj.dandanciu.nachos.machine;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ro.utcluj.dandanciu.utils.collections.ArrayStack;
import ro.utcluj.dandanciu.utils.collections.Stack;

public class OutputDevice extends Device {

	public OutputDevice(Apic apic, int code) {
		super(apic, code);
	}

	private Stack<Character> data = new ArrayStack<Character>();
	char buffer;
	
	Lock lock = new ReentrantLock();
	
	public void outChar(){		
		buffer = data.pop();
		triggerInterrupt();		
	}

	public void addChar(char c){
		this.data.push(c);		
	}

	public void run() {
		System.out.println(buffer);				
	}

	@Override
	public void handle() {
		// TODO Auto-generated method stub
		
	}
}
