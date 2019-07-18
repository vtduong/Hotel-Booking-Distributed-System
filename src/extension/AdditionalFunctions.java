/**
 * 
 */
package extension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Addition server-side functions for the project
 * @author vanduong
 *
 */
public abstract class AdditionalFunctions {

	//each component of the system (except client) must use this clock
	protected volatile Map<String, Integer> clock; 
	
	
	protected AdditionalFunctions( Map<String, Integer> clock) {
		this.clock = clock;
	}
	
	protected AdditionalFunctions() {
		this.clock = new HashMap<String, Integer>();
	}

	/**
	 *
	 * Gets the clock.
	 *
	 * @return the clock
	 */
	public Map<String, Integer> getClock() {
		return clock;
	}
	
	/**
	 * Gets the time of the given process
	 *
	 * @param name the name
	 * @return the time
	 */
	public int getTime(String name) {
		return clock.get(name);
	}
	
	/**
	 * increment the time of the given process by 1 .
	 *
	 * @param name the new time
	 */
	public synchronized void incrementTime(String name) {
		clock.put(name, clock.get(name) + 1);
	}


	/**
	 * FOR TESTING ONLY
	 * Sets the clock.
	 *
	 * @param clock the clock
	 */
	private static synchronized void setClock(Map<String, Integer> clock) {
		clock = clock;
	}

}
