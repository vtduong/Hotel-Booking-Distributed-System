package vspackage.server;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.json.simple.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import extension.AdditionalFunctions;
import ipconfig.IPConfig;
import vspackage.RemoteMethodApp.RemoteMethodPackage.AccessDeniedException;
import vspackage.RemoteMethodApp.RemoteMethodPackage.ClassNotFoundException;
import vspackage.RemoteMethodApp.RemoteMethodPackage.IllegalArgumentException;
import vspackage.RemoteMethodApp.RemoteMethodPackage.NoSuchFieldException;
import vspackage.RemoteMethodApp.RemoteMethodPackage.SecurityException;
import vspackage.bean.Header;
import vspackage.bean.Protocol;
import vspackage.config.Config;
import vspackage.tools.ExtractDate;
import vspackage.tools.JSONParser;
import vspackage.tools.Logger;

public class MethodImpl extends AdditionalFunctions implements Serializable{
	
	private String serverName = null;
	private Logger logger = null;
	private String fullName = null;
	
//	// volatile because of thread safe
//	private volatile Map<String, HashMap<String, Integer>> eventMap = null;
//	
//	/**
//	 * a mapping of event and list of customers, used to keep track events a customer reserves
//	 */
//	private volatile Map<String,HashMap<String, List<String>>> eventCus = null;
	
	protected MethodImpl() throws SecurityException, NoSuchFieldException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException, IOException {
		this(null, null);
	}
	
	
	protected MethodImpl(String name, String fullName) throws IOException, SecurityException, NoSuchFieldException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException{
		super(name);
		serverName = name;
		this.fullName = fullName;
		
		logger = new Logger(name);
		logger.log(2, name + " started.");
		
//		eventMap = new HashMap<String, HashMap<String, Integer>>();
//		eventCus = new HashMap<>();
		this.setStaticValue("eventMap", new HashMap<String, HashMap<String, Integer>>());
		this.setStaticValue("eventCus", new HashMap<>());
		initEventList();
		initCusList();
		
//		ReceiveMessage recevive = new ReceiveMessage(name);
		ReceiveMessage recevive = new ReceiveMessage(serverName);
		Thread thread = new Thread(recevive);
		thread.start();
//		new Thread(new UDPMulticast(serverName)).start();

	}

	protected MethodImpl(String name) throws IOException, RemoteException, SecurityException, NoSuchFieldException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
		this(name, null);
		
	}
	
	
	private void initEventList() throws IOException, SecurityException, NoSuchFieldException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
		String path = Config.getProperty("events");
		
		try(Scanner sc = new Scanner(new File(path));) {
			
			String key = null;
			HashMap<String, Integer> innerMap = new HashMap<String, Integer>();
			
			while(sc.hasNext()) {
				String line = sc.nextLine();
				String[] temp = line.split(" ");
				
				if(temp.length == 1) {
					
					if(key != null) {
						Map newMap = (HashMap) this.getStaticValue("eventMap");
						newMap.put(key, innerMap);
						this.setStaticValue("eventMap", newMap);
//						eventMap.put(key, innerMap);
					}
					
					key = temp[0];
					innerMap = new HashMap<String, Integer>();
				} 
				
				else {
					if(serverName.equalsIgnoreCase(temp[0].substring(0, 3)))
						innerMap.put(temp[0], Integer.parseInt(temp[1]));
				}
				
				if(!sc.hasNext()) {
//					eventMap.put(key, innerMap);
					Map newMap = (HashMap) this.getStaticValue("eventMap");
					newMap.put(key, innerMap);
				}
			}
			
//			System.out.println(serverName + " " + eventMap.toString());
			System.out.println(serverName + " " + this.getStaticValue("eventMap") );
			logger.log(2, "initEventList() : returned None : Completed reading the default event data.");
			
		} catch (IOException e) {
			
			logger.log(0, "initEventList() : returned None : " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	private void initCusList() throws IOException, SecurityException, NoSuchFieldException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
		String path = Config.getProperty("events");
		
		try(Scanner sc = new Scanner(new File(path));) {
			
			String key = null;
			HashMap<String, List<String>> innerMap = new HashMap<String, List<String>>();
			
			while(sc.hasNext()) {
				String line = sc.nextLine();
				String[] temp = line.split(" ");
				
				if(temp.length == 1) {
					
					if(key != null) {
						Map newMap = (HashMap) this.getStaticValue("eventCus");
						newMap.put(key, innerMap);
						this.setStaticValue("eventCus", newMap);
//						eventCus.put(key, innerMap);
					}
					
					key = temp[0];
					innerMap = new HashMap<String, List<String>>();
				} 
				
				else {
					
					innerMap.put(temp[0], new ArrayList<String>());
				}
				
				if(!sc.hasNext()) {
					Map newMap = (HashMap) this.getStaticValue("eventCus");
					newMap.put(key, innerMap);
					this.setStaticValue("eventCus", newMap);
//					eventCus.put(key, innerMap);
				}
			}
			
			logger.log(2, "initCusList() : returned None : Completed reading the default event data.");
			
		} catch (IOException e) {
			
			logger.log(0, "initCusList() : returned None : " + e.getMessage());
			e.printStackTrace();
		}
	}
 	
	
	
	public synchronized String addEvent(String eventID, String eventType, int bookingCapacity) throws vspackage.RemoteMethodApp.RemoteMethodPackage.RemoteException, vspackage.RemoteMethodApp.RemoteMethodPackage.AccessDeniedException, vspackage.RemoteMethodApp.RemoteMethodPackage.ClassNotFoundException, vspackage.RemoteMethodApp.RemoteMethodPackage.IOException {
		String cityCode = eventID.substring(0, 3);
		
		if(cityCode.equalsIgnoreCase(serverName)) {
			try {
				return addEventUDP(eventID, eventType, bookingCapacity);
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
//		else {
//			String status = "Something went wrong";
//			
//			Header head = new Header(4, null, serverName, cityCode, eventID, eventType, bookingCapacity);
//			SendMessage sender;
//			try {
//				sender = new SendMessage(head);
//				status = (String)sender.send();
//			} catch (NumberFormatException | IOException e) {
//				e.printStackTrace();
//			} catch (org.json.simple.parser.ParseException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//			
//			
//			return "From Server " + serverName + status;
//		}	
		
		try {
			logger.log(-1, "addEventUDP(" + eventID + "," + eventType + "," + bookingCapacity +
					") : returned : " + "Event cannot be added");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "From Server " + serverName + "cannot proceed. fail";
	}
	
	private String addEventUDP(String eventID, String eventType, int bookingCapacity) throws SecurityException, IOException{
		boolean isExist;
		try {
			Map eventMap = (HashMap) this.getStaticValue("eventMap");
			Map eventCus = this.getStaticValue("eventCus");
			isExist = ((HashMap) eventMap.get(eventType)).containsKey(eventID);
			if(bookingCapacity < 0) {
				return "capacity must be non-zero. failed to create event";
			}
			
			if(isExist) {
				Map newMap = new HashMap<>(eventMap);
				((HashMap) newMap.get(eventType)).put(eventID, bookingCapacity);
				this.setStaticValue("eventMap", newMap);
//				 eventMap.get(eventType).put(eventID, bookingCapacity);
				
				logger.log(2, "addEventUDP(" + eventID + "," + eventType + "," + bookingCapacity +
						") : returned : " + "Event already exists, capacity is updated successfully");
				
				return "Event already exists, capacity is updated successfully";
			}
			Map newEventMap = new HashMap<>(eventMap);
			((HashMap) newEventMap.get(eventType)).put(eventID, bookingCapacity);
			this.setStaticValue("eventMap", newEventMap);
			Map newEventCus = new HashMap<>(eventCus);
			((HashMap) newEventCus.get(eventType)).put(eventID, new ArrayList<String>());
			this.setStaticValue("eventCus", newEventCus);
//			eventMap.get(eventType).put(eventID, bookingCapacity);
			
			logger.log(2, "addEventUDP(" + eventID + "," + eventType + "," + bookingCapacity +
					") : returned : " + "new event has been added");
			
			return "new event has been successfully added";
		}catch(Exception e) {
			
			e.printStackTrace();
			
			logger.log(0, "addEventUDP(" + eventID + "," + eventType + "," + bookingCapacity +
					") : returned : " + "Something went wrong : " + e.getMessage());
			
			return "Something went wrong. failed";
		}
		
	}
	
//public synchronized String checkAndCancel(){
//	//check if event exists and is booked by cust
//	//if true , create a new header
//	//result = send to OTW
//	//if resul
//	//listen to MTL server to proceed cancel or not
//	//create 
//	(List<String>) ((HashMap) eventCus.get(eventType)).get(eventID);
////	int cap = eventMap.get(eventType).get(eventID);
//	return null;
//}
	
	
	
public synchronized String removeEvent(String eventID, String eventType) throws ClassNotFoundException, SecurityException, vspackage.RemoteMethodApp.RemoteMethodPackage.IOException {
		
		String cityCode = eventID.substring(0, 3);
		
		if(cityCode.equalsIgnoreCase(serverName)) {
			try {
				return removeEventUDP(eventID, eventType);
			} catch (IOException e) {
				String status = "Something went wrong. fail";
				
				return "From Server " + serverName + status;
			}
		}
		
		else {
			String status = "Something went wrong. fail";
			//request remote server
			Header head = new Header(Protocol.REMOTE_REMOVE_EVENT, null, serverName, cityCode, eventID, eventType, 0);
			SendMessage sender;
			try {
				sender = new SendMessage(head);
				status = (String)sender.send();
			} catch (NumberFormatException | IOException e) {
				e.printStackTrace();
			} catch (org.json.simple.parser.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return "From Server " + serverName + status;
			
		}
		
	}
	
	
	
	private String removeEventUDP(String eventID, String eventType) throws SecurityException, IOException{
		boolean hasCustomer;
		try {
			Map eventMap = this.getStaticValue("eventMap");
			boolean isExist = ((HashMap) eventMap.get(eventType)).containsKey(eventID);
			Map eventCus = (HashMap) this.getStaticValue("eventCus");
			
			isExist = ((HashMap) eventMap.get(eventType)).containsKey(eventID);
			if(!isExist) {
				
				logger.log(2, "removeEventUDP(" + eventID + "," + eventType + "," +
						") : returned : " + "event does not exist, no action taken");
				
				
				return "event does not exist, no action taken. fail";
			}
			
			hasCustomer = ((List<String>) ((HashMap) eventCus.get(eventType)).get(eventID)).size() > 0 ? true : false;
			Map newEventMap = new HashMap<>(eventMap);
			((HashMap) newEventMap.get(eventType)).remove(eventID);
			this.setStaticValue("eventMap", newEventMap);
			Map newCusMap = new HashMap<>(eventCus);
			((HashMap) newCusMap.get(eventType)).remove(eventID);
			this.setStaticValue("eventCus", newCusMap);
//			hasCustomer = eventCus.get(eventType).get(eventID).size() > 0 ? true :false;
//			eventMap.get(eventType).remove(eventID);
//			eventCus.get(eventType).remove(eventID);
			if(hasCustomer) {
				
				logger.log(2, "removeEventUDP(" + eventID + "," + eventType +
						") : returned : " + "Event has been canceled and removed");
				
				return "Event has been canceled and removed successfully";
				
			} else {
				
				logger.log(2, "removeEventUDP(" + eventID + "," + eventType +
						") : returned : " + "Event has been removed without side effects");
				
				return "Event has been removed without side effects successfully" ;}
			
		}catch(Exception e) {
			e.printStackTrace();
			
			logger.log(0, "removeEventUDP(" + eventID + "," + eventType  +
					") : returned : " + "Something went wrong : " + e.getMessage());
			
			return "Something went wrong, failed";
		}
//		
	}
	
	
	public synchronized String listEventAvailability(String userID, String eventType) throws vspackage.RemoteMethodApp.RemoteMethodPackage.IOException, vspackage.RemoteMethodApp.RemoteMethodPackage.RemoteException, ClassNotFoundException {
		
		
		try {
		StringBuilder builder = new StringBuilder(listEventAvailabilityUPD(userID, eventType));
	 	builder.append(getRemoteEventsByEventType(Protocol.GET_REMOTE_AVAILABILITY, eventType));
		return "From Server " + serverName +  builder.toString();
		} catch(Exception e) {
			e.printStackTrace();
			return "From Server " + serverName +  "something went wrong. failed";
		}
	}

	private String listEventAvailabilityUPD(String userID, String eventType) throws SecurityException, IOException {
		String availability = "";
		try {
			Map eventMap = this.getStaticValue("eventMap");
			//get result from local server	
			for(Object key: ((HashMap) eventMap.get(eventType)).keySet()) {
				String innerKey = (String)key;	
				//data.put(innerKey, eventMap.get(eventType).get(innerKey));
				availability = availability + innerKey + " : " + ((HashMap) eventMap.get(eventType)).get(innerKey) + "\n";
			}
			
			String returnVal  = availability.length() > 0 ? availability : "No available events";
			
			logger.log(2, "listEventAvailabilityUPD(" + eventType  +
					") : returned : " + returnVal);
			
			
			return returnVal + " success";
		}catch(Exception e) {
			e.printStackTrace();
			
			logger.log(0, "listEventAvailabilityUPD(" + eventType  +
					") : returned : " + "Something went wrong : " + e.getMessage());
			return "Something went wrong. failed";
		}
		
	}
	
	private String getBookingScheduleUDP(String userID) throws IOException  {
		
		//List<String> schedule = new ArrayList<String>();
		String schedule = "";
		try {
			Map eventCus = this.getStaticValue("eventCus");
			
			for(Object key : eventCus.keySet()) {
				String outerKey = (String)key;
				for(Object inKey : ((HashMap) eventCus.get(outerKey)).keySet()) {
					String innerKey = (String)inKey;
					if(((List<String>) ((HashMap) eventCus.get(outerKey)).get(innerKey)).contains(userID)) 
						schedule += innerKey + " " + outerKey + "\n";
				}
			}
			
			String returnVal = schedule.length() > 0 ? schedule : "No event booked\n";
			
			logger.log(2, "getBookingScheduleUDP(" + userID  +
					") : returned : " + returnVal);
			
			
			return returnVal + "success";
			
		}catch(Exception e) {
			e.printStackTrace();
			
			logger.log(0, "getBookingScheduleUDP(" + userID  +
					") : returned : " + "Something went wrong " + e.getMessage());
			return "Something went wrong. failed";
		}
		
		
	}


	private String listRemoteEventAvailability(String eventType) throws ClassNotFoundException, IOException {
		StringBuilder result = new StringBuilder();

		try {
			if(serverName.equalsIgnoreCase("TOR")) {
				Header head = new Header(6, null, serverName, "MTL", null, eventType, 0);
				SendMessage sender = new SendMessage(head);
				result.append( sender.send());
				head = new Header(6, null, serverName, "OTW", null, eventType, 0);
				sender = new SendMessage(head);
				result.append( sender.send());
			}
			else if(serverName.equalsIgnoreCase("MTL")) {
				Header head = new Header(6, null, serverName, "TOR", null, eventType, 0);
				SendMessage sender = new SendMessage(head);
				result.append( sender.send());
				head = new Header(6, null, serverName, "OTW", null, eventType, 0);
				sender = new SendMessage(head);
				result.append( sender.send());
			}else if(serverName.equalsIgnoreCase("OTW")) {
				Header head = new Header(6, null, serverName, "MTL", null, eventType, 0);
				SendMessage sender = new SendMessage(head);
				result.append( sender.send());
				head = new Header(6, null, serverName, "TOR", null, eventType, 0);
				sender = new SendMessage(head);
				result.append( sender.send());
			}
			
			
		} catch (NumberFormatException | IOException e) {
			
			logger.log(0, "listRemoteEventAvailability(" + eventType  +
					") : returned : " + result.toString() + " : " + e.getMessage());
			
			e.printStackTrace();
		} catch (org.json.simple.parser.ParseException e) {
			
			logger.log(0, "listRemoteEventAvailability(" + eventType  +
					") : returned : " + result.toString() + " : " + e.getMessage());
			
			e.printStackTrace();
		}
		
		logger.log(2, "listRemoteEventAvailability(" + eventType  +
				") : returned : " + result.toString());
		
		
		return result.toString();
	}


	private String checkAndBookRemoteEvent(String clientID, String eventID, String eventType ) {
		String result ="";
		SendMessage sender = null;
		try {
			//customer can book at most 3 events in a month
			//get events from the other 2 servers
			result = getRemoteEventsByClientID(Protocol.GET_REMOTE_SCHEDULE, clientID);
			String [] resultLines = result.split("\n");
			
			List<String> events = new ArrayList<String>();
			List<String> idList = new ArrayList<String>();
			int count = 0;
			for(String e : resultLines) {
				String line = e.trim();
				if(line.length() > 0) {
					String code = line.substring(0, 3);
					if(code.equalsIgnoreCase("MTL") || code.equalsIgnoreCase("TOR") || 
							code.equalsIgnoreCase("OTW")) {
						count++;
						idList.add(e);
					}
				}	
			}
			idList.add(eventID);
			int days = 0;
			if(!idList.isEmpty()) {
				ExtractDate tool = new ExtractDate(idList);
				try {
					days = tool.dateDiff();
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			
			if(count == 3 && days <= 30) {
				return "Maximum number of remote event has been booked, no booking done. fail";
			}else {
				if(eventID.substring(0,3).equalsIgnoreCase(this.serverName)) {
					result = this.bookEventUPD(clientID, eventID, eventType);
				}else {
					//forward request to dest server
					Header head = new Header(Protocol.REMOTE_BOOK_EVENT, clientID, serverName, eventID.substring(0,3), eventID, eventType, 0);
					sender =new SendMessage(head); 
					return sender.send();
				}
				
			}
			
		} catch (NumberFormatException | IOException | ClassNotFoundException e) {
			e.printStackTrace();
		} catch (org.json.simple.parser.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	public synchronized String bookEvent(String clientID, String eventID, String eventType) throws IllegalArgumentException, vspackage.RemoteMethodApp.RemoteMethodPackage.IOException{
		//compare client city code with server code
		String clientCityCode = clientID.substring(0, 3);
		String eventCityCode = eventID.substring(0, 3);
		String result = "";
		if(clientCityCode.equalsIgnoreCase(serverName)) {
			if(eventCityCode.equalsIgnoreCase(serverName)) {
				//proceed book event
				try {
					return bookEventUPD(clientID, eventID, eventType);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					
				}
			} else {
				return checkAndBookRemoteEvent(clientID, eventID, eventType);
			}
	
			
		}else {
			if(eventCityCode.equalsIgnoreCase(serverName)) {
				//proceed book event
				return checkAndBookRemoteEvent(clientID, eventID, eventType);
			}else {
				return "event does not belong to this server";
			}
		}
		return "From Server " + serverName + " Something went wrong. fail";
	}
	
	private String getRemoteEventsByEventType(int protocol, String eventType) throws ClassNotFoundException, IOException {
//		StringBuilder result = new StringBuilder();
		String result = "";
		SendMessage sender = null;
		try {
			if(serverName.equalsIgnoreCase("MTL")) {
				//send request to Toronto
				Header head = new Header(protocol, null, this.serverName, "TOR", null, eventType, 0);				
				sender = new SendMessage(head);
//				result.append(sender.send());
				result += "\n" + sender.send();
				System.out.println("events: " + result);
				//send request to Ottawa
				head = new Header(protocol, null, this.serverName, "OTW", null, eventType, 0);
				sender = new SendMessage(head);
//				result.append(event);
				System.out.println("line number : " + 587);
				result += "\n" + sender.send();
				
				System.out.println("events: " + result);

			} else if(serverName.equalsIgnoreCase("TOR")) {
				//send request to Toronto
				Header head = new Header(protocol, null, this.serverName, "MTL", null, eventType, 0);				
				sender = new SendMessage(head);
//				result.append(sender.send());
				result += "\n" + sender.send();
				//send request to Ottawa
				head = new Header(protocol, null, this.serverName, "OTW", null, eventType, 0);
				sender = new SendMessage(head);
//				result.append(sender.send());
				result += "\n" + sender.send();
			} else if(serverName.equalsIgnoreCase("OTW")) {
				//send request to Toronto
				Header head = new Header(protocol, null, this.serverName, "TOR", null, eventType, 0);				
				sender = new SendMessage(head);
//				result.append(sender.send());
				result += "\n" + sender.send();
				//send request to Ottawa
				head = new Header(protocol, null, this.serverName, "MTL", null, eventType, 0);
				sender = new SendMessage(head);
//				result.append(sender.send());
				result += "\n" + sender.send();
			} 
		} catch (NumberFormatException | IOException e) {
			
			logger.log(0, "getRemoteEventsByEventType(" + protocol + "," + eventType  +
					") : returned : " + result + " : " + e.getMessage());
			e.printStackTrace();
			
		} catch (org.json.simple.parser.ParseException e) {
			
			logger.log(0, "getRemoteEventsByEventType(" + protocol + "," + eventType  +
					") : returned : " + result + " : " + e.getMessage());
			e.printStackTrace();
		}
		
		logger.log(2, "getRemoteEventsByEventType(" + protocol + "," + eventType  +
				") : returned : " + result);
		
		return result;
	}


//	private String getRemoteEvents(String clientID) throws ClassNotFoundException {
//		StringBuilder result = new StringBuilder();
//		SendMessage sender = null;
//		try {
//			if(serverName.equalsIgnoreCase("MTL")) {
//				//send request to Toronto
//				Header head = new Header(2, clientID, this.serverName, "TOR", null, null, 0);				
//				sender = new SendMessage(head);
//				result.append(sender.send());
//				//send request to Ottawa
//				head = new Header(2, clientID, this.serverName, "OTW", null, null, 0);
//				sender = new SendMessage(head);
//				result.append(sender.send());
//			} else if(serverName.equalsIgnoreCase("TOR")) {
//				//send request to Toronto
//				Header head = new Header(2, clientID, this.serverName, "MTL", null, null, 0);				
//				sender = new SendMessage(head);
//				result.append(sender.send());
//				//send request to Ottawa
//				head = new Header(2, clientID, this.serverName, "OTW", null, null, 0);
//				sender = new SendMessage(head);
//				result.append(sender.send());
//			} else if(serverName.equalsIgnoreCase("OTW")) {
//				//send request to Toronto
//				Header head = new Header(2, clientID, this.serverName, "TOR", null, null, 0);				
//				sender = new SendMessage(head);
//				result.append(sender.send());
//				//send request to Ottawa
//				head = new Header(2, clientID, this.serverName, "OTW", null, null, 0);
//				sender = new SendMessage(head);
//				result.append(sender.send());
//			} 
//		} catch (NumberFormatException | IOException e) {
//			e.printStackTrace();
//		} catch (org.json.simple.parser.ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return result.toString();
//	}
	
	private String getRemoteEventsByClientID(int prototype, String clientID) throws ClassNotFoundException, IOException {
	System.out.println("inside getRemoteEvents " + prototype + " " + clientID );
		String result = "";
	String clientCityCode = clientID.substring(0, 3);
	SendMessage sender = null;
	try {
		if(clientCityCode.equalsIgnoreCase("MTL")) {
			//send request to Toronto
			if(this.serverName.equalsIgnoreCase("TOR")) {
				result += this.getBookingScheduleUDP(clientID);
			} else {
				Header head = new Header(prototype, clientID, this.serverName, "TOR", null, null, 0);				
				sender = new SendMessage(head);
				result += "\n" + sender.send();
			}
			//send request to Ottawa
			if(this.serverName.equalsIgnoreCase("OTW")) {
				result += this.getBookingScheduleUDP(clientID);
			}else {
				Header head = new Header(prototype, clientID, this.serverName, "OTW", null, null, 0);
				sender = new SendMessage(head);
				result += "\n" + sender.send();
			}
			
		} else if(clientCityCode.equalsIgnoreCase("TOR")) {
			//send request to MTL
			if(this.serverName.equalsIgnoreCase("MTL")) {
				result += this.getBookingScheduleUDP(clientID);
			}else {
				Header head = new Header(prototype, clientID, this.serverName, "MTL", null, null, 0);				
				sender = new SendMessage(head);
				result += "\n" + sender.send();
			}
			
			//send request to Ottawa
			if(this.serverName.equalsIgnoreCase("OTW")) {
				result += this.getBookingScheduleUDP(clientID);
			}else {
				Header head = new Header(prototype, clientID, this.serverName, "OTW", null, null, 0);
				sender = new SendMessage(head);
				result += "\n" + sender.send();
			}
			
		} else if(clientCityCode.equalsIgnoreCase("OTW")) {
			//send request to OTW
			if(this.serverName.equalsIgnoreCase("TOR")) {
				result += this.getBookingScheduleUDP(clientID);
			}else {
				Header head = new Header(prototype, clientID, this.serverName, "TOR", null, null, 0);				
				sender = new SendMessage(head);
				result += "\n" + sender.send();
			}
			
			//send request to Ottawa
			if(this.serverName.equalsIgnoreCase("MTL")) {
				result += this.getBookingScheduleUDP(clientID);
			}else {
				Header head = new Header(prototype, clientID, this.serverName, "MTL", null, null, 0);
				sender = new SendMessage(head);
				result += "\n" + sender.send();
			}
			
		} 
	} catch (NumberFormatException | IOException e) {
		
		logger.log(0, "getRemoteEventsByClientID(" + prototype + "," + clientID  +
				") : returned : " + result + " : " + e.getMessage());
		e.printStackTrace();
	} catch (org.json.simple.parser.ParseException e) {
		
		logger.log(0, "getRemoteEventsByClientID(" + prototype + "," + clientID  +
				") : returned : " + result + " : " + e.getMessage());
		e.printStackTrace();
	}
	
	logger.log(2, "getRemoteEventsByClientID(" + prototype + "," + clientID  +
			") : returned : " + result);
	
	
	return result;
}


	private String bookEventUPD(String clientID, String eventID, String eventType) throws IOException {
		//check if eventID exists
		try {
			String status = "";
			Map eventCus = this.getStaticValue("eventCus");
			Map eventMap = this.getStaticValue("eventMap");
			if(eventCus.get(eventType) == null || !((HashMap) eventCus.get(eventType)).containsKey(eventID)||  !((HashMap) eventMap.get(eventType)).containsKey(eventID))
				return "No event exists. fail";
			List<String> cusList = (List<String>) ((HashMap) eventCus.get(eventType)).get(eventID);
//			int cap = eventMap.get(eventType).get(eventID);
			int curAvailability = (int) ((HashMap) eventMap.get(eventType)).get(eventID);

			if(cusList.contains(clientID)) {//check if event has been booked by the same person
				return "Client has already booked this event. fail";
			}
			if(curAvailability == 0) {//check if event is full
				return "Event is full, cannot be booked. fail";
			}
			if(curAvailability > 0 && !cusList.contains(clientID)) {//else add cus to event's cus list
				Map newEventCus = new HashMap<>(eventCus);
				((List<String>) ((HashMap) newEventCus.get(eventType)).get(eventID)).add(clientID);
				//update availability
				this.setStaticValue("eventCus", newEventCus);
				Map newEventMap = new HashMap<>(eventMap);
				((HashMap) newEventMap.get(eventType)).put(eventID, curAvailability - 1);
				this.setStaticValue("eventMap", newEventMap);
			}
		} catch (Exception e) {
			
			logger.log(0, "bookEventUPD(" + clientID + "," + eventID  + "," + eventType + 
					") : returned : Something went wrong : "  + e.getMessage());
			
			
			e.printStackTrace();
			return "Something went wrong. fail";
		}
		
		logger.log(2, "bookEventUPD(" + clientID + "," + eventID  + "," + eventType + 
				") : returned : Event has been successfully booked");
		
		return "Event has been successfully booked";
		
	}


	
	public synchronized String getBookingSchedule(String clientID) throws ClassNotFoundException, vspackage.RemoteMethodApp.RemoteMethodPackage.IOException {
		try {
			StringBuilder results = new StringBuilder();
			
				results.append(getBookingScheduleUDP(clientID));
				// TODO Auto-generated catch block
			
			//search in remote servers
			//TODO uncomment this when UPD is working
			String res = getRemoteEventsByClientID(Protocol.GET_REMOTE_SCHEDULE, clientID);
			results.append(res);
			
			logger.log(2, "getBookingSchedule(" + clientID + 
					") : returned : " + "From Server " + serverName + results.toString());
			
			return "From Server " + serverName + results.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "From Server " + serverName + "error.... failed";
		}
	}

	
	public synchronized String cancelEvent(String customerID, String eventID, String eventType) throws ClassNotFoundException, vspackage.RemoteMethodApp.RemoteMethodPackage.IOException {
		String cityCode = eventID.substring(0, 3);
		if(serverName.equalsIgnoreCase(cityCode)){//check if event is local
			//remove customer from event
			try {
				return cancelEventUDP(customerID, eventID, eventType);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				String status = "Something went wrong. fail";
				return "From Server " + serverName + status;
			}
		}
		
		else {
			// Request remote server to cancel event
			Header head = new Header(Protocol.REMOTE_CANCEL_EVENT, customerID, serverName, cityCode, eventID, eventType, 0);
			SendMessage sender;
			String status = "Something went wrong. fail";
			try {
				sender = new SendMessage(head);
				status = (String)sender.send();
			} catch (NumberFormatException | IOException e) {
				e.printStackTrace();
			} catch (org.json.simple.parser.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			return "From Server " + serverName + status;
			
		}
		
	}
	
	private String cancelEventUDP(String customerID, String eventID, String eventType) throws IOException {
		boolean isRemoved;
		try {
			Map eventCus = this.getStaticValue("eventCus");
			Map newEventCus = new HashMap<>(eventCus);
			if(eventCus.get(eventType) == null) {
				return "Event does not exist. failed";
			}
			isRemoved = ((List<String>) ((HashMap) newEventCus.get(eventType)).get(eventID)).remove(customerID) ;
			Map eventMap = this.getStaticValue("eventMap");
			if(isRemoved) {
				//update availability
				Map newEventMap = new HashMap<>(eventMap);
				int curAvailability = (int) ((HashMap) eventMap.get(eventType)).get(eventID);
				((HashMap) newEventMap.get(eventType)).put(eventID, curAvailability + 1);
				this.setStaticValue("eventMap", newEventMap);
				
				logger.log(2, "cancelEventUDP(" + customerID + "," + eventID + "," + eventType + 
						") : returned : " + "Event has been canceled");
				
				
				return "Event has been canceled successfully";
				
			} else {
				
				logger.log(2, "cancelEventUDP(" + customerID + "," + eventID + "," + eventType + 
						") : returned : " + "Event belongs to another customer, no cancelation done");
				
				return "Event belongs to another customer, no cancelation done. fail";
			}
		}catch (Exception e) {
			e.printStackTrace();
			logger.log(0, "cancelEventUDP(" + customerID + "," + eventID + "," + eventType + 
					") : returned : " + "Something went wrong : " + e.getMessage());
			return "Something went wrong. failed";
		}
		
	}

	public synchronized String swapEvent(String customerID, String newEventID, String newEventType, String oldEventID, String oldEventType) throws vspackage.RemoteMethodApp.RemoteMethodPackage.RemoteException, vspackage.RemoteMethodApp.RemoteMethodPackage.AccessDeniedException, vspackage.RemoteMethodApp.RemoteMethodPackage.ClassNotFoundException, vspackage.RemoteMethodApp.RemoteMethodPackage.IOException, vspackage.RemoteMethodApp.RemoteMethodPackage.NoSuchFieldException, vspackage.RemoteMethodApp.RemoteMethodPackage.IllegalArgumentException {
		return swapEventUDP(customerID, newEventID, newEventType, oldEventID, oldEventType);

	}
	
	private String swapEventUDP(String customerID, String newEventID, String newEventType, String oldEventID,
			String oldEventType) {
//		StringBuilder result = new StringBuilder();
		String result = "";
		try {
			//check if both events are local
			String oldCityCode = oldEventID.substring(0,3);
			String newCityCode = newEventID.substring(0,3);
			if(this.serverName.equalsIgnoreCase(oldCityCode) && this.serverName.equalsIgnoreCase(newCityCode)) {
				//acquire a lock on this server (since eventCus is static)
				
					synchronized(Class.forName(fullName)) {
						//check both conditions
						Map eventCus = this.getStaticValue("eventCus");
						boolean hasCustomer = ((List<String>) ((HashMap) eventCus.get(oldEventType)).get(oldEventID)).contains(customerID);
						boolean isAvailable = false;
						String result2 = "";
						String result1 = "";
						Map eventMap = this.getStaticValue("eventMap");
						if(eventMap.get(newEventType) != null && (Integer)((HashMap) eventMap.get(newEventType)).getOrDefault(newEventID, 0) > 0) {
							isAvailable = true;
						}
						if(hasCustomer == true) {
							//book new event
							result2 = this.bookEventUPD(customerID, newEventID, newEventType);
							
						}
						if(result2.contains("successfully")) {
							//cancel old event
							result1 = this.cancelEventUDP(customerID, oldEventID, oldEventType);
						}
						if(result1.equalsIgnoreCase("") || result2.equalsIgnoreCase("")) {
							result = result2 + " " + result1;
						}else {
							result = result2 + " " + result1;
						}
						
					}
					logger.log(2, "swapEventUDP(" + customerID + newEventID + "," + newEventType + "," +
					oldEventID + oldEventType +
							") : returned : " + result.toString());
					return result.trim().replaceAll("[\\000]*", "");
				
			} else {//one of the event is not local
				//old event is local, new event is remote
				if(this.serverName.equalsIgnoreCase(oldCityCode) && !this.serverName.equalsIgnoreCase(newCityCode)) {
					synchronized(Class.forName(fullName)) {
						//check isBooked condition
						Map eventCus = this.getStaticValue("eventCus");
						boolean hasCustomer = ((List<String>) ((HashMap) eventCus.get(oldEventType)).get(oldEventID)).contains(customerID);
						String cancelResult = "";
						String bookResult = "";
						if(hasCustomer == true) {
							//check and book remote event
//							SendMessage sender = new SendMessage(new Header(Protocol.BOOK_EVENT, customerID, oldCityCode, newCityCode, newEventID,
//									newEventType, -1));
//							bookResult = sender.send();
//							Header header = new Header(Protocol.BOOK_EVENT, customerID, this.serverName, newEventID.substring(0,3), newEventID, newEventType, 0); 
//							SendMessage sender = new SendMessage(header);
							bookResult = this.bookEvent(customerID, newEventID, newEventType);
						}
						if(bookResult.contains("successfully")) {//cancel old event
							cancelResult = this.cancelEventUDP(customerID, oldEventID, oldEventType);
							result = cancelResult + " " + bookResult;
						}else {
							result = "cannot swap events";
						}
						logger.log(2, "swapEventUDP(" + customerID + newEventID + "," + newEventType + "," +
								oldEventID + oldEventType +
										") : returned : " + result.toString());
						return result.trim().replaceAll("[\\000]*", "");
					}
				} else if(this.serverName.equalsIgnoreCase(newCityCode) &&
						!this.serverName.equalsIgnoreCase(oldCityCode)) {//old event is remote, new event is local
					String cancelResult = "";
					String bookResult = "";
					//check isAvailable condition
					Map eventMap = this.getStaticValue("eventMap");
					int availability = 0;
					if(((HashMap) eventMap.get(newEventType)) != null) {
						availability = (Integer)((HashMap) eventMap.get(newEventType)).getOrDefault(newEventID, 0);
					}
					if(availability > 0) {
						//check and cancel old event remotely
						SendMessage sender = new SendMessage(new Header(Protocol.REMOTE_CANCEL_EVENT, customerID, this.serverName, oldCityCode, oldEventID,
								oldEventType, -1));
						cancelResult = sender.send();
					}
					if(cancelResult.contains("successfully")) {
						//book local event
						bookResult = this.bookEventUPD(customerID, newEventID, newEventType);
						result = cancelResult + " " + bookResult;
					}else {
						result = "cannot swap events. fail";
					}
					
					logger.log(2, "swapEventUDP(" + customerID + newEventID + "," + newEventType + "," +
							oldEventID + oldEventType +
									") : returned : " + result.toString());
					return result.trim().replaceAll("[\\000]*", "");
				}else if(!this.serverName.equalsIgnoreCase(oldCityCode) &&
						!this.serverName.equalsIgnoreCase(newCityCode)) {//both events are remote
					SendMessage sender = new SendMessage(new Header(Protocol.REMOTE_SWAP_EVENT, customerID, this.serverName, oldCityCode, oldEventID,
							 oldEventType, newEventID, newEventType, -1));
					String message = sender.send();
					logger.log(2, "swapEventUDP(" + customerID + newEventID + "," + newEventType + "," +
							oldEventID + oldEventType +
									") : returned : " + message);
					return message.trim().replaceAll("[\\000]*", "");
				}
			}
			
		} catch (SecurityException | IllegalAccessException | IOException | org.json.simple.parser.ParseException | NoSuchFieldException | ClassNotFoundException | IllegalArgumentException | java.lang.ClassNotFoundException | vspackage.RemoteMethodApp.RemoteMethodPackage.IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			logger.log(0, "swapEventUDP(" + customerID + newEventID + "," + newEventType + "," +
					oldEventID + oldEventType +
							") : returned : " + "something went wrong");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "something went wrong. fail";

		
	}
	
	
	

	class ReceiveMessage implements Runnable {
		
		DatagramSocket socket = null;
		int port;
		
		public ReceiveMessage(String serverType) throws NumberFormatException, IOException {
			port = 0;
			
			if(serverType.equalsIgnoreCase("MTL")) {
				//port = Integer.parseInt(Config.getProperty("montreal_udp_port_recv"));
				port = Integer.parseInt(IPConfig.getProperty("mtl_port_vs"));
			}
			
			else if(serverType.equalsIgnoreCase("OTW")) {
				//port = Integer.parseInt(Config.getProperty("ottawa_udp_port_recv"));
				port = Integer.parseInt(IPConfig.getProperty("otw_port_vs"));
			}
			
			else if(serverType.equalsIgnoreCase("TOR")) {
				//port = Integer.parseInt(Config.getProperty("toronto_udp_port_recv"));
				port = Integer.parseInt(IPConfig.getProperty("tor_port_vs"));
			}
			
			this.socket = new DatagramSocket(port);
			
			logger.log(2, "ReceiveMessage(" + serverType + 
					") : returned : " + "None : Init the socket and port " + port);
		}
		
		public void unicastOneWay( String addr, int port, Header header) throws IOException {
			Gson gson = new Gson();
			
			String data = gson.toJson(header);
			
			byte[] msg = data.getBytes();
			
			DatagramPacket packet = new DatagramPacket(msg, msg.length, 
					InetAddress.getByName(addr), port);
			socket.send(packet);
		}
		
		
		public void run() {
			
			try {
				logger.log(2, "Run(" + 
						") : returned : " + "None : Thread started");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			while(true) {
				Thread.currentThread().setName(Integer.toString(port));
				System.out.println("Thread for receive : " + Thread.currentThread().getName());
				byte[] message = new byte[10000];
				DatagramPacket packet = new DatagramPacket(message, message.length);
				
				try {
					Header data = null;
					//ObjectMapper mapper = new ObjectMapper();
					System.out.println("waiting for a request");
					socket.receive(packet);
					String content = new String(message).trim();
					System.out.println("fucking received it: " + content);
					
				
						//content = content.replaceAll("\\uFEFF", "");
						//Object json = new JSONParser().parse(content);
						//JSONObject jsonObj = (JSONObject) json;
						
//						JSONParser parser = new JSONParser(content);
//						Map<String, String> jsonObj = parser.deSerialize();
						
						Gson gson = new Gson();
						data = gson.fromJson(content.trim(), Header.class);
//						Header data = mapper.readValue(new String(message), Header.class);
						
//						data = new Header();
						

					
					/*
					 * The handling message logic here. 
					 */
					
					Object result = null;
					
					if(data.getProtocol() == Protocol.ADD_EVENT) {
						
						try {
							result = addEventUDP(data.getEventID(), data.getEventType(), data.getCapacity());
						} catch (SecurityException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
					
					else if(data.getProtocol() == Protocol.BOOK_EVENT) {
						
						result = bookEvent(data.getUserID(), data.getEventID(), data.getEventType());
						
					}
					
					else if(data.getProtocol() == Protocol.CANCEL_EVENT) {
						
						result = cancelEvent(data.getUserID(), data.getEventID(), data.getEventType());
						
					}
					
					else if(data.getProtocol() == Protocol.EVENT_AVAILABLITY) {
						
						result = listEventAvailability(data.getUserID(), data.getEventType());
						
					}
					
					else if(data.getProtocol() == Protocol.GET_SCHEDULE_EVENT) {
						
						result = getBookingSchedule(data.getUserID());
						
					}
					else if(data.getProtocol() == Protocol.GET_REMOTE_SCHEDULE) {
						result = getBookingScheduleUDP(data.getUserID());
						//send back to the sender, NOT THE FE
						System.out.println("Sending result: " + result);

						byte[] reply = result.toString().getBytes();
						
//						DatagramPacket replyPacket = new DatagramPacket(
//								reply, reply.length, InetAddress.getByName(IPConfig.getProperty("fe_addr")), packet.getPort());//change port number at demo
						DatagramPacket replyPacket = new DatagramPacket(
								reply, reply.length, packet.getAddress(), packet.getPort());//change port number at demo
						socket.send(replyPacket);
						continue;
					}
					else if(data.getProtocol() == Protocol.GET_REMOTE_AVAILABILITY) {
						result = listEventAvailabilityUPD(data.getUserID(), data.getEventType());
						//send back to the sender, NOT THE FE
						System.out.println("Sending result: " + result);

						byte[] reply = result.toString().getBytes();
						
//						DatagramPacket replyPacket = new DatagramPacket(
//								reply, reply.length, InetAddress.getByName(IPConfig.getProperty("fe_addr")), packet.getPort());//change port number at demo
						DatagramPacket replyPacket = new DatagramPacket(
								reply, reply.length, packet.getAddress(), packet.getPort());//change port number at demo
						socket.send(replyPacket);
						continue;
					}
					else if(data.getProtocol() == Protocol.REMOTE_BOOK_EVENT) {
						result = bookEventUPD(data.getUserID(), data.getEventID(), data.getEventType());
						//send back to the sender, NOT THE FE
						System.out.println("Sending result: " + result);

						byte[] reply = result.toString().getBytes();
						
//						DatagramPacket replyPacket = new DatagramPacket(
//								reply, reply.length, InetAddress.getByName(IPConfig.getProperty("fe_addr")), packet.getPort());//change port number at demo
						DatagramPacket replyPacket = new DatagramPacket(
								reply, reply.length, packet.getAddress(), packet.getPort());//change port number at demo
						socket.send(replyPacket);
						continue;
					}
					else if(data.getProtocol() == Protocol.REMOTE_REMOVE_EVENT) {
						result = removeEventUDP(data.getEventID(), data.getEventType());
						//send back to the sender, NOT THE FE
						System.out.println("Sending result: " + result);

						byte[] reply = result.toString().getBytes();
						
//						DatagramPacket replyPacket = new DatagramPacket(
//								reply, reply.length, InetAddress.getByName(IPConfig.getProperty("fe_addr")), packet.getPort());//change port number at demo
						DatagramPacket replyPacket = new DatagramPacket(
								reply, reply.length, packet.getAddress(), packet.getPort());//change port number at demo
						socket.send(replyPacket);
						continue;
					}
					else if(data.getProtocol() == Protocol.REMOTE_CANCEL_EVENT) {
						result = cancelEventUDP(data.getUserID(), data.getEventID(), data.getEventType());
						//send back to the sender, NOT THE FE
						System.out.println("Sending result: " + result);

						byte[] reply = result.toString().getBytes();
						
//						DatagramPacket replyPacket = new DatagramPacket(
//								reply, reply.length, InetAddress.getByName(IPConfig.getProperty("fe_addr")), packet.getPort());//change port number at demo
						DatagramPacket replyPacket = new DatagramPacket(
								reply, reply.length, packet.getAddress(), packet.getPort());//change port number at demo
						socket.send(replyPacket);
						continue;
					}
					else if(data.getProtocol() == Protocol.REMOTE_SWAP_EVENT) {
						result = swapEventUDP(data.getUserID(), data.getNewEventID(), data.getNewEventType(), data.getEventID(), data.getEventType());
						//send back to the sender, NOT THE FE
						System.out.println("Sending result: " + result);

						byte[] reply = result.toString().getBytes();
						
//						DatagramPacket replyPacket = new DatagramPacket(
//								reply, reply.length, InetAddress.getByName(IPConfig.getProperty("fe_addr")), packet.getPort());//change port number at demo
						DatagramPacket replyPacket = new DatagramPacket(
								reply, reply.length, packet.getAddress(), packet.getPort());//change port number at demo
						socket.send(replyPacket);
						continue;
					}
					
					else if(data.getProtocol() == Protocol.REMOVE_EVENT) {
						
						result = removeEvent(data.getEventID(), data.getEventType());
						
					} else if(data.getProtocol() == Protocol.SWAP_EVENT) {
						result = swapEvent(data.getUserID(), data.getNewEventID(), data.getNewEventType(), data.getEventID(), data.getEventType());
					} else if(data.getProtocol() == Protocol.SYNC_REQUEST) {
						System.out.println("Sending data for sync request:");
						//return a header with 2 hashmap of this server
						System.out.println(serverName + " " + MethodImpl.this.getStaticValue("eventMap") );
						System.out.println(serverName + " " + MethodImpl.this.getStaticValue("eventCus") );
						Map<String, HashMap<String, Integer>> eventMap = MethodImpl.this.getStaticValue("eventMap");
						Map<String,HashMap<String, List<String>>> eventCus = MethodImpl.this.getStaticValue("eventCus");
						unicastOneWay(packet.getAddress().getHostAddress(), packet.getPort(), new Header(Protocol.SYNC, eventMap, eventCus));
						continue;
					} else if(data.getProtocol() == Protocol.SYNC) {
						//get the 2 hashmaps from header and set the 2 hashmaps of this server
						System.out.println("Syncing data...");
						Map<String, HashMap<String, Integer>> syncedEventMap = data.getEventMap();
						Map<String,HashMap<String, List<String>>> syncedEventCus = data.getEventCus();
//						MethodImpl.this.setStaticValue("eventMap", syncedEventMap);
//						MethodImpl.this.setStaticValue("eventCus", syncedEventCus);
						System.out.println(serverName + " " + MethodImpl.this.getStaticValue("eventMap") );
						System.out.println(serverName + " " + MethodImpl.this.getStaticValue("eventCus") );
						continue;
					}
					
					
					int sequenceID = data.getSequenceId();
					String ip = InetAddress.getLocalHost().toString().split("/")[1];
//					if(sequenceID == 1) {
//						if(ip.equalsIgnoreCase(IPConfig.getProperty("host2"))) {
//							System.out.println("CRASH");
//							System.out.println("Syncing data..");
//							System.out.println(serverName + " " + MethodImpl.this.getStaticValue("eventMap") );
//							System.out.println(serverName + " " + MethodImpl.this.getStaticValue("eventCus") );
//							continue;//crash = do nothing
//						}
//						if(ip.equalsIgnoreCase(IPConfig.getProperty("host1"))) {
//							result = "incorrect result"; //return incorrect result = software failure
//							System.out.println("Sending result: " + result);
//							System.out.println("Syncing data..");
//							System.out.println(serverName + " " + MethodImpl.this.getStaticValue("eventMap") );
//							System.out.println(serverName + " " + MethodImpl.this.getStaticValue("eventCus") );
//						}
//							
//					}
					System.out.println("Sending result: " + result);

					byte[] reply = result.toString().getBytes();
					
//					DatagramPacket replyPacket = new DatagramPacket(
//							reply, reply.length, InetAddress.getByName(IPConfig.getProperty("fe_addr")), packet.getPort());//change port number at demo
					DatagramPacket replyPacket = new DatagramPacket(
							reply, reply.length, InetAddress.getByName(IPConfig.getProperty("fe_addr")), Integer.parseInt("61000"));//change port number at demo
					socket.send(replyPacket);
					
					System.out.println("Sending reply to FE....");
					
					logger.log(2, "Run(" + 
							") : returned : " + "None : send data from port " + port);
					
					
					
				} catch (IOException | SecurityException | NoSuchFieldException | ClassNotFoundException | IllegalArgumentException | IllegalAccessException | vspackage.RemoteMethodApp.RemoteMethodPackage.IOException | vspackage.RemoteMethodApp.RemoteMethodPackage.RemoteException | AccessDeniedException e) {
					
					try {
						logger.log(0, "Run(" + 
								") : returned : " + "None : " + e.getMessage());
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					e.printStackTrace();
				}
			}
		}
		


		
	
		
	}

	class UDPMulticast implements Runnable{
		private String serverName;
		private int receivePort;
		private MulticastSocket socket;
		UDPMulticast(String serverName){
			this.serverName = serverName;
			try {
				if(serverName.equalsIgnoreCase("MTL")) {
					this.receivePort = Integer.parseInt(IPConfig.getProperty("multicast_mtl_receive_port"));
				}else if(serverName.equalsIgnoreCase("TOR")) {
					this.receivePort = Integer.parseInt(IPConfig.getProperty("multicast_tor_receive_port"));
				}else if(serverName.equalsIgnoreCase("OTW")) {
					this.receivePort = Integer.parseInt(IPConfig.getProperty("multicast_otw_receive_port"));
				}
				
				this.socket=new MulticastSocket(this.receivePort);

			    InetAddress group=InetAddress.getByName(IPConfig.getProperty("multicast_ip_addr"));
			    socket.joinGroup(group);
					
			} catch (NumberFormatException | IOException e) {
				
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			String requestMsg = "";
			try {
				
				byte[] buffer = new byte[1000];
				
				while (true) {
					System.out.println("Waiting for multicast message...");
			         DatagramPacket packet=new DatagramPacket(buffer,
			 	            buffer.length);
			 	    socket.receive(packet);
					System.out.println("Request Received On Server: " + new String((packet.getData())));
					String content = new String((packet.getData())).trim();
					Gson gson = new Gson();
					Header data = gson.fromJson(content.trim(), Header.class);
					
					/*
					 * The handling message logic here. 
					 */
					
					Object result = null;
					
					if(data.getProtocol() == Protocol.ADD_EVENT) {
						
						try {
							result = addEventUDP(data.getEventID(), data.getEventType(), data.getCapacity());
						} catch (SecurityException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
					
					else if(data.getProtocol() == Protocol.BOOK_EVENT) {
						
						result = bookEventUPD(data.getUserID(), data.getEventID(), data.getEventType());
						
					}
					
					else if(data.getProtocol() == Protocol.CANCEL_EVENT) {
						
						result = cancelEventUDP(data.getUserID(), data.getEventID(), data.getEventType());
						
					}
					
					else if(data.getProtocol() == Protocol.EVENT_AVAILABLITY) {
						
						result = listEventAvailability(data.getUserID(), data.getEventType());
						
					}
					
					else if(data.getProtocol() == Protocol.GET_SCHEDULE_EVENT) {
						
						result = getBookingSchedule(data.getUserID());
						
					}
					else if(data.getProtocol() == Protocol.GET_REMOTE_SCHEDULE) {
						result = getBookingScheduleUDP(data.getUserID());
						//send back to the sender, NOT THE FE
						System.out.println("Sending result: " + result);

						byte[] reply = result.toString().getBytes();
						
//						DatagramPacket replyPacket = new DatagramPacket(
//								reply, reply.length, InetAddress.getByName(IPConfig.getProperty("fe_addr")), packet.getPort());//change port number at demo
						DatagramPacket replyPacket = new DatagramPacket(
								reply, reply.length, packet.getAddress(), packet.getPort());//change port number at demo
						socket.send(replyPacket);
						continue;
					}
					else if(data.getProtocol() == Protocol.GET_REMOTE_AVAILABILITY) {
						result = listEventAvailabilityUPD(data.getUserID(), data.getEventType());
						//send back to the sender, NOT THE FE
						System.out.println("Sending result: " + result);

						byte[] reply = result.toString().getBytes();
						
//						DatagramPacket replyPacket = new DatagramPacket(
//								reply, reply.length, InetAddress.getByName(IPConfig.getProperty("fe_addr")), packet.getPort());//change port number at demo
						DatagramPacket replyPacket = new DatagramPacket(
								reply, reply.length, packet.getAddress(), packet.getPort());//change port number at demo
						socket.send(replyPacket);
						continue;
					}
					
					
					else if(data.getProtocol() == Protocol.REMOVE_EVENT) {
						
						try {
							result = removeEventUDP(data.getEventID(), data.getEventType());
						} catch (SecurityException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					} else if(data.getProtocol() == Protocol.SWAP_EVENT) {
						result = swapEventUDP(data.getUserID(), data.getNewEventID(), data.getNewEventType(), data.getEventID(), data.getEventType());
					} else if(data.getProtocol() == Protocol.SYNC_REQUEST) {
						System.out.println("Sending data for sync request:");
						//return a header with 2 hashmap of this server
						System.out.println(serverName + " " + MethodImpl.this.getStaticValue("eventMap") );
						System.out.println(serverName + " " + MethodImpl.this.getStaticValue("eventCus") );
						Map<String, HashMap<String, Integer>> eventMap = MethodImpl.this.getStaticValue("eventMap");
						Map<String,HashMap<String, List<String>>> eventCus = MethodImpl.this.getStaticValue("eventCus");
						unicastOneWay(this.socket, packet.getAddress().getHostAddress(), packet.getPort(), new Header(Protocol.SYNC, eventMap, eventCus));
						continue;
					} else if(data.getProtocol() == Protocol.SYNC) {
						//get the 2 hashmaps from header and set the 2 hashmaps of this server
						System.out.println("Syncing data...");
						Map<String, HashMap<String, Integer>> syncedEventMap = data.getEventMap();
						Map<String,HashMap<String, List<String>>> syncedEventCus = data.getEventCus();
//						MethodImpl.this.setStaticValue("eventMap", syncedEventMap);
//						MethodImpl.this.setStaticValue("eventCus", syncedEventCus);
						try {
							System.out.println(serverName + " " + MethodImpl.this.getStaticValue("eventMap") );
						} catch (SecurityException | NoSuchFieldException | ClassNotFoundException
								| IllegalArgumentException | IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						System.out.println(serverName + " " + MethodImpl.this.getStaticValue("eventCus") );
						continue;
					}
					
					int sequenceID = data.getSequenceId();
					String ip = InetAddress.getLocalHost().toString().split("/")[1];
//					if(sequenceID == 1) {
//						if(ip.equalsIgnoreCase(IPConfig.getProperty("host2"))) {
//							System.out.println("CRASH");
//							System.out.println("Syncing data..");
//							System.out.println(serverName + " " + MethodImpl.this.getStaticValue("eventMap") );
//							System.out.println(serverName + " " + MethodImpl.this.getStaticValue("eventCus") );
//							continue;//crash = do nothing
//						}
//						if(ip.equalsIgnoreCase(IPConfig.getProperty("host1"))) {
//							result = "incorrect result"; //return incorrect result = software failure
//							System.out.println("Sending result: " + result);
//							System.out.println("Syncing data..");
//							System.out.println(serverName + " " + MethodImpl.this.getStaticValue("eventMap") );
//							System.out.println(serverName + " " + MethodImpl.this.getStaticValue("eventCus") );
//						}
//							
//					}
					System.out.println("Sending result: " + result);

					byte[] reply = result.toString().getBytes();
					
//					DatagramPacket replyPacket = new DatagramPacket(
//							reply, reply.length, InetAddress.getByName(IPConfig.getProperty("fe_addr")), packet.getPort());//change port number at demo
					DatagramPacket replyPacket = new DatagramPacket(
							reply, reply.length, InetAddress.getByName(IPConfig.getProperty("fe_addr")), Integer.parseInt("61001"));//change port number at demo
					socket.send(replyPacket);
					
					System.out.println("Sending reply to FE....");
					
					logger.log(2, "Run(" + 
							") : returned : " + "None : send data from port " + receivePort);
					
				} 
				

			} catch (SocketException e) {
				System.out.println("Socket: " + e.getMessage());
			} catch (IOException e) {
				System.out.println("IO: " + e.getMessage());
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (vspackage.RemoteMethodApp.RemoteMethodPackage.IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (vspackage.RemoteMethodApp.RemoteMethodPackage.RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
			}
		}
			
	}
		
	
	class SendMessage {
		
		private DatagramSocket socket = null;
		private int port;
		private Header header = null;
		private String toServer = null;
		private String fromServer = null;
		
		public SendMessage(Header header) throws NumberFormatException, IOException {
			this.header = header;
			port = 0;
			
			int senderPort = 0;
			
			
			// Assign a port for sender
			if(header.getFromServer().equalsIgnoreCase("MTL")) {
				senderPort = Integer.parseInt(Config.getProperty("montreal_udp_port_send"));	
			}
			
			else if(header.getFromServer().equalsIgnoreCase("OTW")) {
				senderPort = Integer.parseInt(Config.getProperty("ottawa_udp_port_send"));
			}
			
			else if(header.getFromServer().equalsIgnoreCase("TOR")) {
				senderPort = Integer.parseInt(Config.getProperty("toronto_udp_port_send"));
			}
			
			
			// Choose the port of the server
			if(header.getToServer().equalsIgnoreCase("MTL")) {
				port = Integer.parseInt(IPConfig.getProperty("mtl_port_vs"));	
			}
			
			else if(header.getToServer().equalsIgnoreCase("OTW")) {
				port = Integer.parseInt(IPConfig.getProperty("otw_port_vs"));
			}
			
			else if(header.getToServer().equalsIgnoreCase("TOR")) {
				port = Integer.parseInt(IPConfig.getProperty("tor_port_vs"));
			}
			
			
			this.socket = new DatagramSocket(senderPort);
			
			logger.log(2, "SendMessage(Header obj" + 
					") : returned : " + "None : Init socket and port " + port);
			
		}
		
		public String send(int protocol, String userID) throws IOException {
			InetAddress addr = InetAddress.getByName("localhost");
			
//			header.setProtocol(protocol);
//			header.setFromServer(fromServer);
//			header.setToServer(toServer);
//			header.setUserID(userID);
//			
			JSONObject jsonData = new JSONObject();
			
			jsonData.put("protocol_type", header.getProtocol());
			jsonData.put("userID", header.getUserID());
			jsonData.put("fromServer", header.getFromServer());
			jsonData.put("toServer", header.getToServer());
			jsonData.put("eventID", header.getEventID());
			jsonData.put("eventType", header.getEventType());
			jsonData.put("newEventID", header.getNewEventID());
			jsonData.put("newEventType", header.getNewEventType());
			jsonData.put("capacity", header.getCapacity());
			
//			ObjectMapper objToJson = new ObjectMapper();
//			String json = objToJson.writeValueAsString(header);
			
			String json = jsonData.toJSONString();
			
			byte[] data = json.getBytes();
			DatagramPacket packet = new DatagramPacket(data, data.length, addr, port);
			
			socket.send(packet);
			
			byte[] statusCode = new byte[8];
			
//			DatagramPacket ack = new DatagramPacket(statusCode, statusCode.length);
//			socket.receive(ack);
			
			//return Integer.parseInt(new String(statusCode));
			socket.disconnect();
			socket.close();
			
			logger.log(2, "SendMessage(" + protocol + "," + userID + 
					") : returned : " + new String(statusCode));
			
			return new String(statusCode);
			
		}
		
		public String send() throws IOException, ClassNotFoundException, org.json.simple.parser.ParseException{
			InetAddress addr = InetAddress.getByName("localhost");

//			ObjectMapper objToJson = new ObjectMapper();
			
			JSONObject jsonData = new JSONObject();
			jsonData.put("protocol_type", header.getProtocol());
			jsonData.put("userID", header.getUserID());
			jsonData.put("fromServer", header.getFromServer());
			jsonData.put("toServer", header.getToServer());
			jsonData.put("eventID", header.getEventID());
			jsonData.put("eventType", header.getEventType());
			jsonData.put("newEventID", header.getNewEventID());
			jsonData.put("newEventType", header.getNewEventType());
			jsonData.put("capacity", header.getCapacity());
			
			
//			String json = objToJson.writeValueAsString(header);
			
			String json = jsonData.toJSONString();
			
			byte[] data = json.getBytes();
			DatagramPacket packet = new DatagramPacket(data, data.length, addr, port);
			
			socket.send(packet);
			
			byte[] result = new byte[10000];
			System.out.print(new String(result));
			
			DatagramPacket ack = new DatagramPacket(result, result.length);
			socket.receive(ack);
			
			String temp = new String(result);
			System.out.println(temp);
			
			socket.disconnect();
			socket.close();
			
			logger.log(2, "SendMessage(" +
					") : returned : " + new String(result));
			
			return new String(result);
			

		}
	}
	
	private  HashMap getStaticValue(final String fieldName) throws SecurityException, NoSuchFieldException, ClassNotFoundException,
    IllegalArgumentException, IllegalAccessException {
		
		try {
		// Get the private field
		final Field field = Class.forName(fullName).getDeclaredField(fieldName);
		// Allow modification on the field
		field.setAccessible(true);
		
		
		// Return the Obect corresponding to the field
		
			return (HashMap) field.get(Class.forName(fullName));
		} catch (java.lang.IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (java.lang.ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (java.lang.NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (java.lang.SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void unicastOneWay(MulticastSocket socket, String addr, int port, Header header) throws IOException {
Gson gson = new Gson();
		
		String data = gson.toJson(header);
		
		byte[] msg = data.getBytes();
		
		DatagramPacket packet = new DatagramPacket(msg, msg.length, 
				InetAddress.getByName(addr), port);
		socket.send(packet);
		
	}


	public void unicastOneWay(String hostAddress, int port, Header header) {
		
		
	}


	private  void setStaticValue(final String fieldName, final Object newValue) throws SecurityException, NoSuchFieldException,
    ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
		try {
			// Get the private String field
			final Field field = Class.forName(fullName).getDeclaredField(fieldName);
			// Allow modification on the field
			field.setAccessible(true);
			// Get
			final Object oldValue = field.get(Class.forName(fullName));
			// Sets the field to the new value
			field.set(oldValue, newValue);
		
		} catch(Exception e) {
			e.printStackTrace();
		}

	}
	
	
}

