package vspackage.tools;

import java.util.Map;

import vspackage.bean.Header;
import vspackage.bean.Protocol;

public class Adapter {
	
	public static String objectToString(byte[] message) {
		String content = new String(message);
		JSONParser parser = new JSONParser(content);
		Map<String, String> jsonObj = parser.deSerialize();
		
		Header data = new Header();
	
		
		String capacity = jsonObj.get("capacity").trim();
		String eventID = (String) jsonObj.get("eventID");
		String eventType = (String) jsonObj.get("eventType");
		String newEventID = (String) jsonObj.get("newEventID");
		String newEventType = (String) jsonObj.get("newEventType");
		String fromServer = (String) jsonObj.get("fromServer");
		String toServer = (String) jsonObj.get("toServer");
		int protocol = Integer.parseInt(jsonObj.get("PROTOCOL_TYPE"));
		String userID = (String) jsonObj.get("userID");
		
		String output = "";
		
		switch(protocol) {
			
		case Protocol.BOOK_EVENT:
			output = "bookEvent" + ";" + userID + ";" + eventID + ";" + eventType;
			break;
		
		case Protocol.ADD_EVENT:
			output = "Add_Event" + ";" + eventID + ";" + eventType + ";" + capacity;
			break;
			
		case Protocol.CANCEL_EVENT:
			output = "CE" + ";" + userID + ";" + eventID + ";" + eventType;
			break;
		
		case Protocol.EVENT_AVAILABLITY:
			output = "listEventAvailability" + ";" + eventType;
			break;
			
		case Protocol.GET_SCHEDULE_EVENT:
			output = "getBookingSchedule" + ";" + userID;
			break;
			
		case Protocol.REMOVE_EVENT:
			output = "remove_event" + ";" + eventID + ";" + eventType;
			break;
		
		case Protocol.SWAP_EVENT:
			output = "swapEvent" + ";" + userID + ";" + newEventID + ";" + newEventType + ";" + eventID + ";" + eventType;
			break;
			
		case Protocol.SYNC:
			output = "SYNC;SYNC";
			break;
			
		case Protocol.SYNC_REQUEST:
			output = "SYNC_REQUEST;SYNC_REQUEST";
			break;
			
		default:
			output = null;
			
		}
		
		return output;
	}
}
