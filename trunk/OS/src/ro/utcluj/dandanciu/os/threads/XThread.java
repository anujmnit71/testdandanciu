package ro.utcluj.dandanciu.os.threads;

public interface XThread extends Runnable{
	
	<T extends XThread> void fork(T current);
	
	void finish();
	
	void yield();
	
	void sleep(long ticks);
	
	<T extends XThread> void join(T current);

}
