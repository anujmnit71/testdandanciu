/**
 * 
 */
package ro.utcluj.dandanciu.nachos.machine.vm;

import ro.utcluj.dandanciu.nachos.machine.Word;

/**
 * @author Dan Danciu
 * 
 */
public abstract class TlbEntry {

	/**
	 * Loads the data from the given word into the specified entry
	 * 
	 * @param data
	 * @param entry
	 */
	abstract void load(Word data);

}
