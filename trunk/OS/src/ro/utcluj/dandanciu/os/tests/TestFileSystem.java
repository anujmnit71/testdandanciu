package ro.utcluj.dandanciu.os.tests;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import ro.utcluj.dandanciu.nachos.machine.Machine;
import ro.utcluj.dandanciu.os.threads.Kernel;
import ro.utcluj.dandanciu.os.threads.XThreadAbstract;
import ro.utcluj.dandanciu.os.threads.kernel.ThreadsKernel;
import ro.utcluj.dandanciu.os.userprogs.syscall.XUserThread;
import junit.framework.TestCase;

public class TestFileSystem extends TestCase {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(TestOS.class);
	
	private Kernel kernel;
	
	public void setUp() {
		PropertyConfigurator.configure("log4j.properties");
		
		Machine.getInstance().start();
		
		kernel = Kernel.getKernel();
	}
	
	public void test1(){
		XThreadAbstract t1 = new XUserThread() {

			@Override
			protected void restoreContext() {}

			@Override
			public void run() {
				System.out.println(">>>TEST ..........");
				int fid = create("file1.txt");
				byte[] buffer = "Ana are pere!".getBytes();
				write(fid, buffer, buffer.length);
				close(fid);
				fid = create("file2.txt");
				close(fid);
				unlink("file2.txt");
				fid = open("file1.txt");
				byte[] newBuffer = new byte[20];
				read(fid, newBuffer, newBuffer.length);
				System.out.println("READ: "+new String(newBuffer).trim());
				System.out.println(">>>TEST FINISHED..........");
			}

			@Override
			protected void saveContext() {}
			
		};
		t1.fork(kernel);
	}
	
	public static void main(String[] args) {
		TestFileSystem tos = new TestFileSystem();
		tos.setUp();
		tos.test1();
	}
}
