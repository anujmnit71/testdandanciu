/**
 * 
 */
package ro.utcluj.dandanciu.nachos.machine.vm;

import ro.utcluj.dandanciu.nachos.machine.Word;
import ro.utcluj.dandanciu.nachos.machine.vm.exceptions.EntryNotFoundException;
import ro.utcluj.dandanciu.nachos.machine.vm.exceptions.IllegalMemoryAccessException;
import ro.utcluj.dandanciu.nachos.machine.vm.exceptions.NotCachableException;
import ro.utcluj.dandanciu.nachos.machine.vm.exceptions.TlbDirtyException;
import ro.utcluj.dandanciu.nachos.machine.vm.exceptions.TlbInvalidException;

/**
 * @author Dan Danciu
 * 
 */
public interface TranslationLookasideBuffer {

	int translate(int virtualAddress, int asid, boolean writting)
			throws EntryNotFoundException, IllegalMemoryAccessException,
			TlbInvalidException, TlbDirtyException, NotCachableException;

	void store(Word data, int position);
	
	Word get(int positio);

}
