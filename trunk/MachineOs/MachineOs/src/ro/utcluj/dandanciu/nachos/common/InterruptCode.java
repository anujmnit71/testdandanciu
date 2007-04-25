package ro.utcluj.dandanciu.nachos.common;

public class InterruptCode {
	
	private int code = 0;
	
	private InterruptCode(int code) {
		this.code = code;
	}
	
	public static final InterruptCode TICK = new InterruptCode(48);
	
	public int ordinal(){
		return code;
	}

}
