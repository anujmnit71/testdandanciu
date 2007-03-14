/**
 * 
 */
package ro.utcluj.dandanciu.nachos.machine;

/**
 * @author Dan Danciu
 *
 */
public abstract class Device {
	
	private boolean inService = false;
	
	/**
	 * @return the inService
	 */
	public boolean isInService() {
		return inService;
	}

	/**
	 * @param inService the inService to set
	 */
	public void setInService(boolean inService) {
		this.inService = inService;
	}

}
