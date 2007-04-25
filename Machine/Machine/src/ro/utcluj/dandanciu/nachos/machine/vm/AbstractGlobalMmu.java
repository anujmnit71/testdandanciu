/**
 * 
 */
package ro.utcluj.dandanciu.nachos.machine.vm;

import ro.utcluj.dandanciu.nachos.machine.Word;

/**
 * @author Dan Danciu
 * 
 */
public abstract class AbstractGlobalMmu {

	public abstract Word tlbProbe();

	public abstract Word tlbRead(int index);

	public abstract void tlbWriteRandom(Word entryWord);

	public abstract void tlbWriteIndex(Word entryWord, int index);

}
