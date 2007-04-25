package ro.utcluj.dandanciu.os.threads;

public interface XThread extends Runnable{
	
	int getThreadId();
	
	<T extends XThread> void fork(T current);
	
	void finish();
	
	void yield();
	
	void sleep(long ticks);
	
	<T extends XThread> void join(T current);
	
	<T extends XThread> void switchFrom(T previous);
	
	void resume();	

	void setName(String name);
	
	String getName();
}
