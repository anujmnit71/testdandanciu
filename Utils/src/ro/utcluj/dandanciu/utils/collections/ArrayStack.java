package ro.utcluj.dandanciu.utils.collections;

import java.util.ArrayList;
import java.util.List;

public class ArrayStack<E> implements Stack<E> {
	
	private List<E> data = new ArrayList<E>();

	public boolean empty() {
		return (data.size() == 0);
	}

	public E pop() {
		E e = data.get(data.size() - 1);
		data.remove(data.size() - 1);	
		return e;
	}

	public void push(E e) {
		data.add(e);
	}

}
