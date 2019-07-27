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
public abstract class AdditionalFunctions implements Clock{

	//each component of the system (except client) must use this clock
	protected volatile Map<String, Integer> clock; 
	
	
	protected AdditionalFunctions( Map<String, Integer> clock) {
		this.clock = clock;
	}
	
	protected AdditionalFunctions() {
		this.clock = new HashMap<String, Integer>();
		clock.put("FE", 0);
		clock.put("SE", 0);
		clock.put("RM1", 0);
		clock.put("RM2", 0);
		clock.put("RM3", 0);
		clock.put("RM4", 0);
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
	public int getLocalTime(String name) {
		return clock.get(name);
	}
	
	/**
	 * increment the time of the given process by 1 .
	 *
	 * @param name the new time
	 */
	public synchronized void incrementLocalTime(String name) {
		clock.put(name, clock.get(name) + 1);
	}
	
	/**
	 * Update local time of a component given its name and the clock in the received message
	 *
	 * @param name the name
	 * @param messageTime the message time
	 * @return the updated local clock
	 */
	public synchronized Map<String, Integer> updateLocalClock(String name, Map<String, Integer> messageClock) {
		int newTime = 0;
		//compare local clock with message clock and update local clock accordingly
		for(Map.Entry<String, Integer> entry : messageClock.entrySet()) {
//			int localTime = this.clock.get(entry.getKey());
			newTime = Math.max(this.clock.get(entry.getKey()), messageClock.get(entry.getKey()));
			this.clock.put(name, newTime);
		}
		
		return this.clock;
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
