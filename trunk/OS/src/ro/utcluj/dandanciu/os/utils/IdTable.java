package ro.utcluj.dandanciu.os.utils;

import java.util.Arrays;

/**
 * This class represents a table for holding objects at a certain id, when the
 * object is no longer in use the id can be used again to reference a new object.
 * 
 * @author Dan Danciu
 *
 * @param <T> the type of the objects hold in this table
 */
public class IdTable<T> {

	T[] objects;
	private int increment;
	
	/**
	 * Constructs an id table with a certain initial size and a size increment
	 * @param initialSize the initial size of the table
	 * @param increment the size increment, this is used when there are no
	 * free spots in the table, and the table size has to be increesed.
	 */
	public IdTable(int initialSize, int increment) {
		this.increment = increment;
		objects = (T[]) new Object[initialSize];
		Arrays.fill(objects, null);
	}
	
	/**
	 * Returns the <tt>T<tt> object with the given id.
	 * @param id
	 * @return
	 */
	public T get(int id) {
		return objects[id];
	}
	
	/**
	 * Adds a new object to the table
	 * @param object the object to add
	 * @return the id of the object
	 */
	public int put(T object) {
		int id = nextId(this, increment);
		objects[id] = object;
		return id;
	}
	
	/**
	 * Removes an object from the table, the id will be free to use later
	 * @param id the of the object to clear
	 */
	public void clear(int id) {
		objects[id] = null;
	}
	
	/**
	 * Returns the size of the table, this isn't necessary equal with the number
	 * of object in the table
	 * @return the size of the table
	 */
	public int size() {
		return objects.length;
	}
	
	/**
	 * Returns a String representation of the table.
	 */
	public String toString() {
		return Arrays.toString(objects);
	}
	
	/**
	 * Used for getting the next id from the table
	 * @param <T> the tipe of the objects in the table
	 * @param table the table for which we want the next id
	 * @param increment the size increment in case there are no
	 * free spots in the table
	 * @return the next id in the table
	 */
	private static <T> int nextId(IdTable<T> table, int increment) {		
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
