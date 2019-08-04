/**
 * 
 */
package replicaManager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import ipconfig.IPConfig;
import vspackage.RemoteMethodApp.RemoteMethodPackage.SecurityException;
import vspackage.bean.Header;
import vspackage.bean.Protocol;
import vspackage.config.Config;
import vspackage.tools.JSONParser;
import vspackage.tools.Logger;

/**
 * Replica Manager
 * @author vanduong
 *
 */
public class RM {
	private Logger logger = null;
	private String hostIP = null;
	/**
	 * @throws IOException 
	 * @throws NumberFormatException 
	 * 
	 */
	public RM() throws NumberFormatException, IOException {
		this.hostIP = InetAddress.getLocalHost().toString().split("/")[1];
		logger = new Logger(hostIP);
		logger.log(2, hostIP + " started.");
		new Thread(new ReceiveMessage(hostIP)).start();;
	}

	/**
	 * @param args
	 * @throws IOException 
	 * @throws NumberFormatException 
	 */
	public static void main(String[] args) throws NumberFormatException, IOException {
		new RM();

	}
	
class ReceiveMessage implements Runnable {
		
		DatagramSocket socket = null;
		int port;
		
		public ReceiveMessage(String ip) throws NumberFormatException, IOException {
			port = 0;
			
			//TODO add port number
			
			this.socket = new DatagramSocket(port);
			
			logger.log(2, "ReceiveMessage(" + ip + 
					") : returned : " + "None : Init the socket and port " + port);
		}
		
		
		public void run() {
			
			try {
				logger.log(2, "Run(" + 
						") : returned : " + "None : Thread started");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			while(true) {
				Thread.currentThread().setName(Integer.toString(port));
				System.out.println("Thread for receive : " + Thread.currentThread().getName());
				byte[] message = new byte[10000];
				DatagramPacket packet = new DatagramPacket(message, message.length);
				
				try {
					ObjectMapper mapper = new ObjectMapper();
					socket.receive(packet);
					String content = new String(message);
					//content = content.replaceAll("\\uFEFF", "");
					//Object json = new JSONParser().parse(content);
					//JSONObject jsonObj = (JSONObject) json;
					
					JSONParser parser = new JSONParser(content);
					Map<String, String> jsonObj = parser.deSerialize();
					
//					Header data = mapper.readValue(new String(message), Header.class);
					
					Header data = new Header();
					
					
					data.setCapacity(Integer.parseInt(jsonObj.get("capacity").trim()));
					data.setEventID((String) jsonObj.get("eventID"));
					data.setEventType((String) jsonObj.get("eventType"));
					data.setNewEventID((String) jsonObj.get("newEventID"));
					data.setNewEventType((String) jsonObj.get("newEventType"));
					data.setFromServer((String) jsonObj.get("fromServer"));
					data.setToServer((String) jsonObj.get("toServer"));
					data.setProtocol(Integer.parseInt(jsonObj.get("protocol_type")));
					data.setUserID((String) jsonObj.get("userID"));
					data.setSequenceId(Integer.parseInt(jsonObj.get("sequenceId").trim()));
					
					
					/*
					 * The handling message logic here. 
					 */
					
					Object result = null;
					
					if(data.getProtocol() == Protocol.CRASH) {
						if(data.getToServer().split(":")[0].equalsIgnoreCase(hostIP)) {
							//send request to synch data
							
						}
					}
					if(data.getProtocol() == Protocol.FAIL) {
						
					}
					
					//TODO add operations
					byte[] reply = result.toString().getBytes();
					
					DatagramPacket replyPacket = new DatagramPacket(reply, reply.length, packet.getAddress(), packet.getPort());
					socket.send(replyPacket);
					
					
					logger.log(2, "Run(" + 
							") : returned : " + "None : send data from port " + port);
					
					
					
				} catch (IOException e) {
					
					try {
						logger.log(0, "Run(" + 
								") : returned : " + "None : " + e.getMessage());
					} catch (IOException e1) {
						
						e1.printStackTrace();
					}
					
					e.printStackTrace();
				}
			}
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

}
