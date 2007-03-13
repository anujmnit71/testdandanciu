package ro.utcluj.dandanciu.nachos.machine;

public class ProcessorState {
	
	private int code = -1;

	private ProcessorState(int i){
		code  = i;
	}
	
	public static ProcessorState IDLE = new ProcessorState(1);

	public static ProcessorState RUNNING = new ProcessorState(2);

	public static ProcessorState IN_INTERRUPT = new ProcessorState(3);

	@Override
	public boolean equals(Object obj) {
		return this.code == ((ProcessorState) obj).code;
	}
}
