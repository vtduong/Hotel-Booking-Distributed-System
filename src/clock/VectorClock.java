/**
 * 
 */
package clock;

import java.util.HashMap;

/**
 * Global Clock object of the distributed system
 * @author vanduong
 *
 */
public class VectorClock {
	private HashMap<String, Integer> clock = null;
	

	/**
	 * 
	 */
	public VectorClock() {
		clock = new HashMap<String, Integer>();
	}

	
	/**
	 *
	 * Gets the clock.
	 *
	 * @return the clock
	 */
	public HashMap<String, Integer> getClock() {
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
	public synchronized void setTime(String name) {
		clock.put(name, clock.get(name) + 1);
	}


	/**
	 * FOR TESTING ONLY
	 * Sets the clock.
	 *
	 * @param clock the clock
	 */
	private static synchronized void setClock(HashMap<String, Integer> clock) {
		clock = clock;
	}
	
	

}
