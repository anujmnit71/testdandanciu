package ro.utcluj.dandanciu.os.threads.servers;

import ro.utcluj.dandanciu.os.threads.drivers.InputDeviceDriver;
import ro.utcluj.dandanciu.os.threads.drivers.OutputDeviceDriver;

/**
 * 
 * After initialization the kernel should have nothing to do.
 * Should use kernel's process.
 * 
 * @author Dan Danciu
 *
 */
public class TTYServer {
	
	InputDeviceDriver inputDeviceDriver = null;
	OutputDeviceDriver outputDeviceDriver = null;
	
	public void write(String what) {
		//TODO implement TTYServer.write
	}
	
	public String read() {
		return null;
	}
	
	public boolean login() {
		//TODO: implement login
		return false;
	}
	
	public void console() {
		//TODO: console
	}
	
	public void tty() {
		while(!login());
		console();
	}
}
