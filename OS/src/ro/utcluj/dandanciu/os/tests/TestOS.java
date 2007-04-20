package ro.utcluj.dandanciu.os.tests;

import org.apache.log4j.Logger;

import org.apache.log4j.PropertyConfigurator;

import ro.utcluj.dandanciu.nachos.machine.Machine;
import ro.utcluj.dandanciu.nachos.ostomachine.ThreadContextHelper;
import ro.utcluj.dandanciu.os.threads.Kernel;
import ro.utcluj.dandanciu.os.threads.XThreadAbstract;

import junit.framework.TestCase;

public class TestOS extends TestCase {
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
		XThreadAbstract t1 = new XThreadAbstract() {

			@Override
			protected void restoreContext() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void run() {
				this.name = "TestOs";
				for(int i = 0; i < 5; i++){
					System.out.println("Ana are "+ i + " mere: ");
					this.sleep(300);
				}
			}

			@Override
			protected void saveContext() {
				// TODO Auto-generated method stub
				
			}
			
		};
		t1.fork(kernel);
	}
	
	public static void main(String[] args) {
		TestOS tos = new TestOS();
		tos.setUp();
		tos.test1();
	}
}
