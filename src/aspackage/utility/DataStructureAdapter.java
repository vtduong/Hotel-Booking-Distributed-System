package aspackage.utility;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aspackage.beans.EventInformation;
public class DataStructureAdapter {
	
	public synchronized HashMap<String, ArrayList<EventInformation>> convertCustomerMap(HashMap<String,HashMap<String,List<String>>> customerMap) {
		HashMap<String, ArrayList<EventInformation>> customerBook = new HashMap<String, ArrayList<EventInformation>>();
		for(String eventType: customerMap.keySet()) {
			HashMap<String, List<String>> temp =customerMap.get(eventType);
			for(String eventID:temp.keySet()) {
				ArrayList<String> customers = (ArrayList<String>) temp.get(eventID);
				EventInformation eventRec = new EventInformation();
				eventRec.setEventId(eventID);
				eventRec.setEventType(eventType);
				eventRec.setEventTime(Character.toString(eventID.charAt(3)));
				eventRec.setEventlocation(eventID.substring(0, 3));
				eventRec.setEventDate(eventID.substring(4, 10));
				for(String s:customers) {
					if(customerBook.containsKey(s)) {
						customerBook.get(s).add(eventRec);
						
					}else {
						ArrayList<EventInformation> templist = new ArrayList<EventInformation>();
						templist.add(eventRec);
						customerBook.put(s, templist);
						
						
					}
					
				}
				
			}
			
		}
		return customerBook;
		
	}
	
	/*
	 * 
	 */
	public synchronized HashMap<String, ArrayList<HashMap<String, EventInformation>>> convertEventMap(Map<String, HashMap<String, Integer>> eventMap) {
		HashMap<String, ArrayList<HashMap<String, EventInformation>>> eventBook = new HashMap<String, ArrayList<HashMap<String, EventInformation>>>();
		for(String eventType :eventMap.keySet()) {
			ArrayList<HashMap<String, EventInformation>> listToUpdate = new ArrayList<HashMap<String, EventInformation>>();
			HashMap<String, Integer> temp =eventMap.get(eventType);
			for(String eventID : temp.keySet()){
				EventInformation eventRec = new EventInformation();
				eventRec.setEventId(eventID);
				eventRec.setEventType(eventType);
				eventRec.setEventTime(Character.toString(eventID.charAt(3)));
				eventRec.setEventlocation(eventID.substring(0, 3));
				eventRec.setEventDate(eventID.substring(4, 10));
				eventRec.setCapasity(temp.get(eventID));
				HashMap<String, EventInformation> toAdd = new HashMap<String, EventInformation>();
				toAdd.put(eventID, eventRec);
				listToUpdate.add(toAdd);
			}
			eventBook.put(eventType, listToUpdate);
		}
		return eventBook;
		
	}
	
	/*
	 * 
	 */
	public synchronized Map<String, HashMap<String, Integer>> convertEventMapToHeaderFormat(HashMap<String, ArrayList<HashMap<String, EventInformation>>> eventBook) {
		Map<String, HashMap<String, Integer>> eventMap = new HashMap<String, HashMap<String, Integer>>();
		for(String eventType :eventBook.keySet()) {
			ArrayList<HashMap<String, EventInformation>> tempmap = eventBook.get(eventType);
			for(HashMap<String, EventInformation> m :tempmap) {
				if(eventMap.containsKey(eventType)) {
					for(String s : m.keySet()) {
						EventInformation eventRec = m.get(s);
						HashMap<String, Integer> temp = eventMap.get(eventType);
						temp.put(eventRec.getEventId(), eventRec.getCapasity());
						eventMap.put(eventType, temp);
					}	
				}else {
					for(String s : m.keySet()) {
						EventInformation eventRec = m.get(s);
						HashMap<String, Integer> temp = new HashMap<String, Integer>();
						temp.put(eventRec.getEventId(), eventRec.getCapasity());
						eventMap.put(eventType, temp);
					}	
				}
				
			}
			
			
		}
		return eventMap;
	}
	
	/*
	 * 
	 */
	public synchronized HashMap<String,HashMap<String,List<String>>> convertCustomerMapToHeaderFormat(HashMap<String, ArrayList<EventInformation>> customerBook) {
		HashMap<String,HashMap<String,List<String>>> customerMap = new HashMap<String,HashMap<String,List<String>>>();
		for(String customerID:customerBook.keySet()) {
			ArrayList<EventInformation> eventRecs = customerBook.get(customerID);
			for(EventInformation ev :eventRecs) {
				HashMap<String,List<String>> inmap = new HashMap<String,List<String>>();
				if(customerMap.containsKey(ev.getEventType())) {
					inmap=customerMap.get(ev.getEventType());
					if(inmap.containsKey(ev.getEventId())) {
						inmap.get(ev.getEventId()).add(customerID);
					}else {
						ArrayList<String> templist = new ArrayList<String>();
						templist.add(customerID);
						inmap.put(ev.getEventId(), templist);
					} 
					customerMap.put(ev.getEventType(), inmap);
					
				}else {
					ArrayList<String> templist = new ArrayList<String>();
					templist.add(customerID);
					inmap.put(ev.getEventId(), templist);
					customerMap.put(ev.getEventType(), inmap);
				}
				
			}
			
		}
		return customerMap;
	}

}
