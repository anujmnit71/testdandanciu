package ro.utcluj.dandanciu.os.threads.tests;

import org.apache.log4j.PropertyConfigurator;

import ro.utcluj.dandanciu.nachos.machine.Machine;
import ro.utcluj.dandanciu.os.threads.Kernel;
import ro.utcluj.dandanciu.os.threads.XThreadAbstract;

import junit.framework.TestCase;

public class TestOS extends TestCase {
	
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
				for(int i = 0; i < 5; i++){
					System.out.println("ana are "+ i + " mere");
					this.sleep(3);
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
