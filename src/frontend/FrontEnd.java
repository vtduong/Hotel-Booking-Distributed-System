package frontend;

import java.io.IOException;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
	
	private static final long serialVersionUID = 1L;
	
	
	Map<String, Integer> clock = new HashMap<String, Integer>();
	
	
	public FrontEnd() {
		super();
	}
	
	
	private Queue<String> getMessages() throws NumberFormatException, IOException {
		Queue<String> queue = new LinkedList<String>();
		
		ReceiveFromHost fromHostOne = new ReceiveFromHost(
				Integer.parseInt(IPConfig.getProperty("fm_waiting_reply_one")),  
				queue, Thread.currentThread());
		
		ReceiveFromHost fromHostTwo = new ReceiveFromHost(
				Integer.parseInt(IPConfig.getProperty("fm_waiting_reply_two")),  
				queue, Thread.currentThread());
		
		ReceiveFromHost fromHostThree = new ReceiveFromHost(
				Integer.parseInt(IPConfig.getProperty("fm_waiting_reply_three")),  
				queue, Thread.currentThread());
		
		
		ReceiveFromHost fromHostFour = new ReceiveFromHost(
				Integer.parseInt(IPConfig.getProperty("fm_waiting_reply_four")),  
				queue, Thread.currentThread());
		
		Thread one = new Thread(fromHostOne);
		Thread two = new Thread(fromHostTwo);
		Thread three = new Thread(fromHostThree);
		Thread four = new Thread(fromHostFour);
		
		ExecutorService service = Executors.newCachedThreadPool();
		service.execute(one);
		service.execute(two);
		service.execute(three);
		service.execute(four);
		
		service.shutdown();
		
		try {
			Thread.sleep(Integer.parseInt(IPConfig.getProperty("timeout_for_rm")));
			
		} catch(InterruptedException e) {
			
			System.out.print("Received all the messages from the server....");
		}
		
		return queue;
	}

	
	// Idea : Receive method waits for the reply from all the servers
	// and then store the results in the queue. We use the queue to 
	// get the correct result 
	private Map<String, List<String>> verify(Queue<String> queue) {
		
		int successCount = 0;
		int failCount = 0;
		
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		//Map<String, String> successServer = new HashMap<String, String>();
		//Map<String, String> failServer = new HashMap<String, String>();
		
		//String successServerNames = "";
		//String failServerNames = "";
		
		List<String> successServerNames = new ArrayList<String>();
		List<String> failServerNames = new ArrayList<String>();
		
		for(String str : queue) {
			
			if(str.contains("success")) {
				successCount++;
				
				String[] temp = str.split(":");
				
				
				successServerNames.add(temp[1]);		
				
			} else {
				failCount++;
				
				String[] temp = str.split(":");
				
				failServerNames.add(temp[1]);
			}
			
		}
		
		
		String result = successCount > failCount? "success" : "failed";
		
		List<String> storeResult = new ArrayList<String>();
		storeResult.add(result);
		
		map.put("result", storeResult);
		map.put("success", successServerNames);
		map.put("failed", failServerNames);
		
		return map;
		
	}
	
	
	public String addEvent (String eventID, String eventType, int bookingCapacity) {
		
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
		
		Queue<String> queue = null;
		
		try {
			
			SendToSequencer sender = new SendToSequencer(header);
			sender.send();
			
			// Grabbing replies from all the servers.
			queue = getMessages();
			
			Map<String, List<String>> map = verify(queue);
			
			// TODO send message to rm.
			
			String fault = map.get("success").size() > map.get("failed").size() ? "failed" : "success";
			if(map.get(fault).size() > 0) {
				
				MulticastRM multicast = new MulticastRM(map.get(fault));
				multicast.multicast();
				
			}
			
			
			return map.get("result").get(0);
			
		} catch(Exception e) {
			
			e.printStackTrace();
	
		}	
		
		
		
		// TODO verification part;
		
		
		return null;
	}


	public String removeEvent (String eventID, String eventType) {
		
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
		
		Queue<String> queue = null;
		
		try {
			
			SendToSequencer sender = new SendToSequencer(header);
			sender.send();
			
			// Grabbing replies from all the servers.
			queue = getMessages();
			
			Map<String, List<String>> map = verify(queue);
			
			// TODO send message to rm.
			
			String fault = map.get("success").size() > map.get("failed").size() ? "failed" : "success";
			if(map.get(fault).size() > 0) {
				
				MulticastRM multicast = new MulticastRM(map.get(fault));
				multicast.multicast();
				
			}
			
			
			return map.get("result").get(0);
			
		} catch(Exception e) {
			
			e.printStackTrace();
	
		}	
		
		return null;
	}


	public String listEventAvailability (String eventType) {
		
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
		Queue<String> queue = null;
		
		try {
			
			SendToSequencer sender = new SendToSequencer(header);
			sender.send();
			
			// Grabbing replies from all the servers.
			queue = getMessages();
			
			Map<String, List<String>> map = verify(queue);
			
			// TODO send message to rm.
			
			String fault = map.get("success").size() > map.get("failed").size() ? "failed" : "success";
			if(map.get(fault).size() > 0) {
				
				MulticastRM multicast = new MulticastRM(map.get(fault));
				multicast.multicast();
				
			}
			
			
			return map.get("result").get(0);
			
		} catch(Exception e) {
			
			e.printStackTrace();
	
		}
		return null;
	}


	public String bookEvent (String customerID, String eventID, String eventType) {
		
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
		
		
		Queue<String> queue = null;
		
		try {
			
			SendToSequencer sender = new SendToSequencer(header);
			sender.send();
			
			// Grabbing replies from all the servers.
			queue = getMessages();
			
			Map<String, List<String>> map = verify(queue);
			
			// TODO send message to rm.
			
			String fault = map.get("success").size() > map.get("failed").size() ? "failed" : "success";
			if(map.get(fault).size() > 0) {
				
				MulticastRM multicast = new MulticastRM(map.get(fault));
				multicast.multicast();
				
			}
			
			
			return map.get("result").get(0);
			
		} catch(Exception e) {
			
			e.printStackTrace();
	
		}	
		
		return null;
	}


	public String getBookingSchedule (String customerID) {
		
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
		
		Queue<String> queue = null;
		
		try {
			
			SendToSequencer sender = new SendToSequencer(header);
			sender.send();
			
			// Grabbing replies from all the servers.
			queue = getMessages();
			
			Map<String, List<String>> map = verify(queue);
			
			
			
			String fault = map.get("success").size() > map.get("failed").size() ? "failed" : "success";
			if(map.get(fault).size() > 0) {
				
				MulticastRM multicast = new MulticastRM(map.get(fault));
				multicast.multicast();
				
			}
			
			
			return map.get("result").get(0);
			
		} catch(Exception e) {
			
			e.printStackTrace();
	
		}	
		return null;
	}


	public String swapEvent (String customerID, String newEventID, String newEventType, String oldEventID, String oldEventType) {
		
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
	
	private Queue<String> queue = null;
	private Thread thread = null;
	
	//private String addr = null;
	//private int from;
	private int to;
	
	public ReceiveFromHost(int to, Queue<String> queue, Thread thread) {
		this.queue = queue;
		this.thread = thread;
		//this.from = from;
		this.to = to;
		//this.addr = addr;
	}
	
	private void receive() throws SocketException {
		
		DatagramSocket socket = new DatagramSocket(this.to);
		byte[] packet = new byte[101];
		DatagramPacket datagram = new DatagramPacket(packet, packet.length);
		
		try {
			
			socket.setSoTimeout(Integer.parseInt(IPConfig.getProperty("timeout_for_rm")));
			
			
			socket.receive(datagram);
			
			queue.add(new String(packet) + ":" + datagram.getSocketAddress() + ":" + datagram.getPort());
			
			if(queue.size() == Integer.parseInt(IPConfig.getProperty("total_rm"))) {
				thread.interrupt();
			
			}
			
		} catch(SocketTimeoutException e) {
			
			queue.add("crashed" + ":" + datagram.getSocketAddress() + ":" + datagram.getPort());
		
		} catch(Exception e) {
			
			queue.add("failure" + ":" + datagram.getSocketAddress() + ":" + datagram.getPort());
		
		} finally {
			
			socket.close();
		}
	}
	
	
	@Override
	public void run() {
		
		try {
			
			receive();
			
		} catch (SocketException e) {
			
			e.printStackTrace();
		}
	}

}


class SendToSequencer {
	
	private DatagramSocket socket = null;
	private int port;
	private Header header = null;
	
	public SendToSequencer(Header header) {
		
		try {
			int selfPort = Integer.parseInt(IPConfig.getProperty("unicast_fe_port"));
			socket = new DatagramSocket(selfPort);
			this.header = header;
			this.port = Integer.parseInt(IPConfig.getProperty("sequencer_receive_port"));
		} catch (Exception e) {
			
			e.printStackTrace();
		} 
	}

	
	@SuppressWarnings("unchecked")
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


class MulticastRM {
	
	private List<String> failedAddr = null;
	
	MulticastRM(List<String> failedAddr) {
		
		this.failedAddr = failedAddr;
	}
	
	public void multicast() throws NumberFormatException, IOException {
		
		int totalRM = Integer.parseInt(IPConfig.getProperty("total_rm")); 
		
		String rm_one_addr = IPConfig.getProperty("rm_one");
		String rm_two_addr = IPConfig.getProperty("rm_two");
		String rm_three_addr = IPConfig.getProperty("rm_three");
		String rm_four_addr = IPConfig.getProperty("rm_four");
		
		int rm_one_port = Integer.parseInt(IPConfig.getProperty("port_rm_one"));
		int rm_two_port = Integer.parseInt(IPConfig.getProperty("port_rm_two"));
		int rm_three_port = Integer.parseInt(IPConfig.getProperty("port_rm_three"));
		int rm_four_port = Integer.parseInt(IPConfig.getProperty("port_rm_four"));
		
		String failedServers = "";
		
		for(String str : failedAddr)
			failedServers = failedServers + str + ",";
		
		failedServers = failedServers.substring(0, failedServers.length() - 1);
		
		UnicastRM unicastOne = new UnicastRM(rm_one_addr, rm_one_port, failedServers);
		UnicastRM unicastTwo = new UnicastRM(rm_two_addr, rm_two_port, failedServers);
		UnicastRM unicastThree = new UnicastRM(rm_three_addr, rm_three_port, failedServers);
		UnicastRM unicastFour = new UnicastRM(rm_four_addr, rm_four_port, failedServers);
		
		unicastOne.unicast();
		unicastTwo.unicast();
		unicastThree.unicast();
		unicastFour.unicast();
		
		//TODO unicast
	}
}


class UnicastRM {
	
	private String addr = null;
	private String data = "";
	private int port = 0;
	
	UnicastRM(String addr, int port, String data) {
		this.addr = addr;
		this.port = port;
		this.data = data;
	}
	
	public void unicast() throws NumberFormatException, IOException {
		int selfPort = Integer.parseInt(IPConfig.getProperty("unicast_fe_port"));
		DatagramSocket socket = new DatagramSocket(selfPort);
		
		byte[] msg = data.getBytes();
		
		DatagramPacket packet = new DatagramPacket(msg, msg.length, 
				InetAddress.getByName(this.addr), this.port);
		
		socket.send(packet);
		socket.close();
	}

}