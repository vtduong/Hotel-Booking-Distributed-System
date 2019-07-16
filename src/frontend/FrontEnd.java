package frontend;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.SynchronousQueue;

import org.json.simple.JSONObject;

import ipconfig.IPConfig;
import vspackage.bean.Header;
import vspackage.config.Config;

public class FrontEnd {
	
	
	public String addEvent (String eventID, String eventType, int bookingCapacity) {
		SynchronousQueue queue = new SynchronousQueue();
		//TODO
		return null;
	}


	public String removeEvent (String eventID, String eventType) {
		SynchronousQueue queue = new SynchronousQueue();
		//TODO
		return null;
	}


	public String listEventAvailability (String eventType) {
		SynchronousQueue queue = new SynchronousQueue();
		//TODO
		return null;
	}


	public String bookEvent (String customerID, String eventID, String eventType) {
		SynchronousQueue queue = new SynchronousQueue();
		//TODO
		return null;
	}


	public String getBookingSchedule (String customerID) {
		SynchronousQueue queue = new SynchronousQueue();
		//TODO
		return null;
	}


	public String cancelEvent (String customerID, String eventID, String eventType) {
		SynchronousQueue queue = new SynchronousQueue();
		//TODO
		return null;
	}


	public String swapEvent (String customerID, String newEventID, String newEventType, String oldEventID, String oldEventType) {
		SynchronousQueue queue = new SynchronousQueue();
		//TODO
		return null;
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
