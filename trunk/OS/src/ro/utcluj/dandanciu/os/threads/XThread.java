package ro.utcluj.dandanciu.os.threads;

public interface XThread extends Runnable{
	
	void fork(Runnable runnable);
	
	void finish();
	
	void yeld();
	
	void sleep();
	
	void join();

}
