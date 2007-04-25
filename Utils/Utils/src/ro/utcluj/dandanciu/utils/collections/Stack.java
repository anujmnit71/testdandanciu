/**
 * 
 */
package ro.utcluj.dandanciu.utils.collections;

/**
 * @author Dan Danciu
 *
 */
public interface Stack<E> {
	
	boolean empty();
	
	void push(E e);
	
	E pop();

}
