package ro.utcluj.dandanciu.os.utils;

import java.util.Arrays;

public class PersistentTable<T> {

	T[] objects;
	private int increment;
	
	public PersistentTable(int initialSize, int increment) {
		this.increment = increment;
		objects = (T[]) new Object[initialSize];
		Arrays.fill(objects, null);
	}
	
	public T get(int i) {
		return objects[i];
	}
	
	public int put(T object) {
		int id = nextId(this, increment);
		objects[id] = object;
		return id;
	}
	
	public void clean(int i) {
		objects[i] = null;
	}
	
	public int size() {
		return objects.length;
	}
	
	public String toString() {
		return Arrays.toString(objects);
	}
	
	private static <T> int nextId(PersistentTable<T> table, int increment) {		
		for(int i = 0; i < table.objects.length; i++) {
			if(table.objects[i] == null) {
				return i;
			}
		}
		
		T[] newArray = (T[]) new Object[table.objects.length + increment];
		Arrays.fill(newArray, null);
		for(int i = 0; i < table.objects.length; i++) {
			newArray[i] = table.objects[i];
		}
		int id = table.objects.length;
		table.objects = newArray;
		return id;
	}
	
}
