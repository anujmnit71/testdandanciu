package ro.utcluj.dandanciu.os.threads.util;

public class MutableObject {
	Object value = null;
	
	public void setValue(Object value) {
		this.value = value;
	}
	
	public int intValue() {
		return ((Integer) value).intValue();
	}
}
