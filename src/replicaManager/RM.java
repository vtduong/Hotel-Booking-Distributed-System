/**
 * 
 */
package replicaManager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

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
	/**
	 * @throws IOException 
	 * @throws NumberFormatException 
	 * 
	 */
	public RM() throws NumberFormatException, IOException {
		String ip = InetAddress.getLocalHost().toString().split("/")[1];
		logger = new Logger(ip);
		logger.log(2, ip + " started.");
		new Thread(new ReceiveMessage(ip)).start();;
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
		
		public ReceiveMessage(String serverType) throws NumberFormatException, IOException {
			port = 0;
			
			//TODO add port number
			
			this.socket = new DatagramSocket(port);
			
			logger.log(2, "ReceiveMessage(" + serverType + 
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

}
