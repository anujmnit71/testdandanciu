package ro.utcluj.dandanciu.os.threads;

public class ThreadState {

	int code;
	
	private ThreadState(int code) {
		this.code = code;
	}
	
	public static final ThreadState UNKNOWN = new ThreadState(-1);

	public static final ThreadState CREATED = new ThreadState(0);
	
	public static final ThreadState RUNNING = new ThreadState(1);
	
	public static final ThreadState READY = new ThreadState(2);
	
	public static final ThreadState BLOCKED = new ThreadState(3);
	
}
