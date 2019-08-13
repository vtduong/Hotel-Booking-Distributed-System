package vspackage.tools;

import com.google.gson.Gson;

import aspackage.clientServer.Util;
import vspackage.bean.Header;
import vspackage.bean.Protocol;

public class Adapter {
	
	public static String objectToString(byte[] message) {
		String content = new String(message);
		
		Gson gson = new Gson();
		Header data = new Header();

		data = gson.fromJson(content.trim(), Header.class);
		
		
		String output = "";
		
		switch(data.getProtocol()) {
			
		case Protocol.BOOK_EVENT:
			output = "bookEvent1" + ";" + data.getUserID() + ";" + data.getEventID() + ";" + data.getEventType();
			break;
		
		case Protocol.ADD_EVENT:
			output = Util.ADD_EVENT + ";" + data.getEventID() + ";" + data.getEventType() + ";" + data.getCapacity();
			break;
			
		case Protocol.CANCEL_EVENT:
			output = Util.CANCEL_EVENT1 + ";" + data.getUserID() + ";" + data.getEventID() + ";" + data.getEventType();
			break;
		
		case Protocol.EVENT_AVAILABLITY:
			output = "listEventAvailability1" + ";" + data.getEventType();
			break;
			
		case Protocol.GET_SCHEDULE_EVENT:
			output = "getBookingSchedule1" + ";" + data.getUserID();
			break;
			
		case Protocol.REMOVE_EVENT:
			output = Util.REM_EVENT + ";" + data.getEventID() + ";" + data.getEventType();
			break;
		
		case Protocol.SWAP_EVENT:
			output = "swapEvent" + ";" + data.getUserID() + ";" + data.getNewEventID() + ";" + data.getNewEventType() + ";" +  data.getEventID() + ";" + data.getEventType();
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
