package ro.utcluj.dandanciu.os.utils;

import java.util.ArrayList;
import java.util.Arrays;

public class Utils {

	public static int nextId(ArrayList which, int capacityIncrement) {
		int i;
		for(i = 0; i < which.size(); i++) {
			if(which.get(i) == null) {
				return i;
			}
		}
		//no free spots in the table, need to increase size
		which.ensureCapacity(capacityIncrement + i - 1);
		return i;
	}

	public static <T extends Object> int nextId(T[] objects, int increment) {
		
		for(int i = 0; i < objects.length; i++) {
			if(objects[i] == null) {
				return i;
			}
		}
		
		T[] newArray = (T[]) new Object[objects.length + increment];
		Arrays.fill(newArray, null);
		for(int i = 0; i < objects.length; i++) {
			newArray[i] = objects[i];
		}
		int id = objects.length;
		objects = newArray;
		return id;
	}

}
