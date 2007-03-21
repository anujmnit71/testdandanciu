/**
 * 
 */
package ro.utcluj.dandanciu.nachos.machine.vm;


/**
 * @author Dan Danciu
 *
 */
public abstract class AbstractTlb implements TranslationLookasideBuffer {

	protected int size;

	public AbstractTlb(int size) {
		super();
		this.size = size;
	}
	
	
	
}
