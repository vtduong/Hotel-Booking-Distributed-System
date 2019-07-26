package frontend;

import java.io.IOException;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.SynchronousQueue;

import org.json.simple.JSONObject;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import FEApp.FEMethodHelper;
import FEApp.FEMethodPOA;
import extension.Clock;
import ipconfig.IPConfig;
import vspackage.bean.Header;
import vspackage.bean.Protocol;

public class FrontEnd extends FEMethodPOA implements Serializable, Clock{
	Map<String, Integer> clock = new HashMap<String, Integer>();
	
	public String addEvent (String eventID, String eventType, int bookingCapacity) {
		SynchronousQueue queue = new SynchronousQueue();
		
		Header header = new Header();
		header.setCapacity(bookingCapacity);
		header.setEventID(eventID);
		header.setEventType(eventType);
		header.setFromServer(eventID.substring(0, 3));
		header.setToServer(eventID.substring(0, 3));
		header.setUserID(null);
		header.setNewEventID(null);
		header.setNewEventType(null);
		header.setProtocol(Protocol.ADD_EVENT);
		
		SendToSequencer sender = new SendToSequencer(header);
		// TODO send and receive
		
		
		return null;
	}


	public String removeEvent (String eventID, String eventType) {
		SynchronousQueue queue = new SynchronousQueue();
		
		Header header = new Header();
		header.setCapacity(0);
		header.setEventID(eventID);
		header.setEventType(eventType);
		header.setFromServer(eventID.substring(0, 3));
		header.setToServer(eventID.substring(0, 3));
		header.setUserID(null);
		header.setNewEventID(null);
		header.setNewEventType(null);
		header.setProtocol(Protocol.REMOVE_EVENT);
		
		//TODO send and receive
		return null;
	}


	public String listEventAvailability (String eventType) {
		SynchronousQueue queue = new SynchronousQueue();
		
		Header header = new Header();
		header.setCapacity(0);
		header.setEventID(null);
		header.setEventType(eventType);
		header.setFromServer(null);
		header.setToServer(null);
		header.setUserID(null);
		header.setNewEventID(null);
		header.setNewEventType(null);
		header.setProtocol(Protocol.EVENT_AVAILABLITY);
		
		//TODO send and receive
		return null;
	}


	public String bookEvent (String customerID, String eventID, String eventType) {
		SynchronousQueue queue = new SynchronousQueue();
		
		Header header = new Header();
		header.setCapacity(0);
		header.setEventID(eventID);
		header.setEventType(eventType);
		header.setFromServer(customerID.substring(0, 3));
		header.setToServer(eventID.substring(0, 3));
		header.setUserID(customerID);
		header.setNewEventID(null);
		header.setNewEventType(null);
		header.setProtocol(Protocol.BOOK_EVENT);
		
		
		//TODO send and receive
		
		return null;
	}


	public String getBookingSchedule (String customerID) {
		SynchronousQueue queue = new SynchronousQueue();
		
		Header header = new Header();
		header.setCapacity(0);
		header.setEventID(null);
		header.setEventType(null);
		header.setFromServer(customerID.substring(0, 3));
		header.setToServer(null);
		header.setUserID(customerID);
		header.setNewEventID(null);
		header.setNewEventType(null);
		header.setProtocol(Protocol.GET_SCHEDULE_EVENT);
		
		//TODO send and receive
		return null;
	}


	public String cancelEvent (String customerID, String eventID, String eventType) {
		SynchronousQueue queue = new SynchronousQueue();
		
		Header header = new Header();
		header.setCapacity(0);
		header.setEventID(eventID);
		header.setEventType(eventType);
		header.setFromServer(customerID.substring(0, 3));
		header.setToServer(eventID.substring(0, 3));
		header.setUserID(customerID);
		header.setNewEventID(null);
		header.setNewEventType(null);
		header.setProtocol(Protocol.CANCEL_EVENT);
		
		//TODO send and receive
		return null;
	}


	public String swapEvent (String customerID, String newEventID, String newEventType, String oldEventID, String oldEventType) {
		SynchronousQueue queue = new SynchronousQueue();
		
		Header header = new Header();
		header.setCapacity(0);
		header.setEventID(oldEventID);
		header.setEventType(oldEventType);
		header.setFromServer(null);
		header.setToServer(null);
		header.setUserID(customerID);
		header.setNewEventID(newEventID);
		header.setNewEventType(newEventType);
		header.setProtocol(Protocol.SWAP_EVENT);
		
		//TODO send and receive
		
		return null;
	}

	public static void main(String[] args) {
		
		try {
			// create and initialize the ORB //
			ORB orb = ORB.init(args, null);
			
			// get reference to rootpoa &amp; activate
			POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
			rootpoa.the_POAManager().activate();

			// create servant and register it with the ORB
			FrontEnd feObj = new FrontEnd();
			

			// get object reference from the servant
			org.omg.CORBA.Object ref = rootpoa.servant_to_reference(feObj);
			
			
			// and cast the reference to a CORBA reference
			FEApp.FEMethod href = FEMethodHelper.narrow(ref);
			// get the root naming context
			// NameService invokes the transient name service
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			
			// Use NamingContextExt, which is part of the
			// Interoperable Naming Service (INS) specification.
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

			// bind the Object Reference in Naming
			NameComponent path[] = ncRef.to_name("FE");
			ncRef.rebind(path, href);

			System.out.println("FE Server ready and waiting ...");
			
			// wait for invocations from clients
			for (;;) {
				orb.run();
			}
		}

		catch (Exception e) {
			System.err.println("ERROR: " + e);
			e.printStackTrace(System.out);
			
	        System.out.println("Exception in FE.main: " + e);
	        
		}


	}


	@Override
	public int getLocalTime(String name) {
		// TODO Auto-generated method stub
		return this.clock.get(name);
	}


	@Override
	public void incrementLocalTime(String name) {
		this.clock.put(name, this.clock.get(name) + 1);
		
	}


	@Override
	public Map<String, Integer> updateLocalClock(String name, Map<String, Integer> messageClock) {
		int newTime = 0;
		//compare local clock with message clock and update local clock accordingly
		for(Map.Entry<String, Integer> entry : messageClock.entrySet()) {
//			int localTime = this.clock.get(entry.getKey());
			newTime = Math.max(this.clock.get(entry.getKey()), messageClock.get(entry.getKey()));
			this.clock.put(name, newTime);
		}
		
		return this.clock;
	}
	
}


class ReceiveFromHost implements Runnable {
	
	private SynchronousQueue queue = null;
	
	public ReceiveFromHost(SynchronousQueue queue) {
		this.queue = queue;
	}
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		// store the results in the queue
	}

}


class SendToSequencer {
	
	private DatagramSocket socket = null;
	private int port;
	private Header header = null;
	
	public SendToSequencer(Header header) {
		
		try {
			this.header = header;
			this.port = Integer.parseInt(IPConfig.getProperty("sequencer_receive_port"));
		} catch (Exception e) {
			
			e.printStackTrace();
		} 
	}

	public String send() throws IOException {

		//TODO
		String sequencerAddr = IPConfig.getProperty("sequencer_addr");
		
		InetAddress addr = InetAddress.getByName(sequencerAddr);
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
		
		String json = jsonData.toJSONString();
		
		byte[] data = json.getBytes();
		DatagramPacket packet = new DatagramPacket(data, data.length, addr, port);
		
		byte[] statusCode = new byte[10000];
		
		DatagramPacket ack = new DatagramPacket(statusCode, statusCode.length);
		socket.receive(ack);
		
		
		socket.disconnect();
		socket.close();
		
//		logger.log(2, "SendMessage(" + protocol + "," + userID + 
//				") : returned : " + new String(statusCode));
		
		return new String(statusCode);

	}
	
}
