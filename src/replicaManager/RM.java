/**
 * 
 */
package replicaManager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

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
	private List<String> otherIPs = null;
	private List<String> workingIPs = null;
	/**
	 * @throws IOException 
	 * @throws NumberFormatException 
	 * 
	 */
	public RM() throws NumberFormatException, IOException {
		this.hostIP = InetAddress.getLocalHost().toString().split("/")[1];
		System.out.println(hostIP);
		otherIPs = new ArrayList<String>();
		this.addOtherIPs(this.hostIP);
		this.workingIPs.addAll(this.otherIPs);
//		logger = new Logger(hostIP);
//		logger.log(2, hostIP + " started.");
		new Thread(new ReceiveMessage(hostIP)).start();
	}

	private void addOtherIPs(String hostIP) throws IOException {
		String rm1 = IPConfig.getProperty("rm_one");
		String rm2 = IPConfig.getProperty("rm_two");
		String rm3 = IPConfig.getProperty("rm_three");
		String rm4 = IPConfig.getProperty("rm_four");
		if(hostIP.equalsIgnoreCase(rm1)) {
			this.otherIPs.add(IPConfig.getProperty("rm_two"));
			this.otherIPs.add(IPConfig.getProperty("rm_three"));
			this.otherIPs.add(IPConfig.getProperty("rm_four"));
		}else if(hostIP.equalsIgnoreCase(rm2)) {
			this.otherIPs.add(IPConfig.getProperty("rm_one"));
			this.otherIPs.add(IPConfig.getProperty("rm_three"));
			this.otherIPs.add(IPConfig.getProperty("rm_four"));
		}else if(hostIP.equalsIgnoreCase(rm3)) {
			this.otherIPs.add(IPConfig.getProperty("rm_one"));
			this.otherIPs.add(IPConfig.getProperty("rm_two"));
			this.otherIPs.add(IPConfig.getProperty("rm_four"));
		}else if(hostIP.equalsIgnoreCase(rm4)) {
			this.otherIPs.add(IPConfig.getProperty("rm_one"));
			this.otherIPs.add(IPConfig.getProperty("rm_three"));
			this.otherIPs.add(IPConfig.getProperty("rm_one"));
		}
		
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
		String hostIP = null;
		int port;
		
		public ReceiveMessage(String ip) throws NumberFormatException, IOException {
			port = Integer.parseInt(IPConfig.getProperty("port_rm"));
			this.hostIP = ip;
			//TODO add port number
			
			this.socket = new DatagramSocket(port);
			
			logger.log(2, "ReceiveMessage(" + ip + 
					") : returned : " + "None : Init the socket and port " + port);
		}
		
		public Header unicast(String addr, int port, Header header) throws NumberFormatException, IOException{
			Gson gson = new Gson();
			
			String data = gson.toJson(header);
			
			byte[] msg = data.getBytes();
			
			DatagramPacket packet = new DatagramPacket(msg, msg.length, 
					InetAddress.getByName(addr), port);
			socket.send(packet);
			
			byte [] buffer = new byte[1000];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
			
			socket.receive(reply);
			String content = new String(packet.getData());
			return  gson.fromJson(content, Header.class);

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
					String content = new String(packet.getData());
					Gson gson = new Gson();
					Header data = gson.fromJson(content, Header.class);
					
					List<String> errorList = data.getError();//the string is of IP:port
					List<String> incorrectList = data.getIncorrect();
					List<String> crashList = data.getCrash();
					
					
					
					
					/*
					 * The handling message logic here. 
					 */
					
					Object result = null;
					if(crashList.size() > 0) {
						for(String s : crashList) {
							String ip = s.split(":")[0];
							String port = s.split(":")[1];
							if(ip.equalsIgnoreCase(hostIP)) {
								//send a sync message to a working host and the corresponding server
								List<Header> headerList = new ArrayList<Header>();
								List<Map<String, HashMap<String, Integer>>> eventMapList = new ArrayList<Map<String, HashMap<String, Integer>>>();
								List<Map<String,HashMap<String, List<String>>>> eventCusList = new ArrayList<Map<String,HashMap<String, List<String>>>>();
								Header head = new Header(Protocol.SYNC_REQUEST, null, null);
								//send unicast to other hosts
								for(int i = 1; i < 4; i++) {
									//skip itself
									if(this.hostIP.equalsIgnoreCase(IPConfig.getProperty("host"+i))) {
										continue;
									}
									Header responseHead = unicast(IPConfig.getProperty("host"+i), 										Integer.parseInt(IPConfig.getProperty(port)), head);
									eventMapList.add(responseHead.getEventMap());
									
								}
								Map<String, HashMap<String, Integer>> eventMap = getCorrectResult(eventMapList);
								//send sync message to the right server based on port given by the FE
							} else {
								//TODO log the event
							}
									
						}
					}
					
					if(incorrectList.size() > 0) {
						for(String s : incorrectList) {
							
						}
					}
					
					if(errorList.size() > 0) {
						for(String s : errorList) {
							
						}
					}
					
					
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

		private Map<String, HashMap<String, Integer>> getCorrectResult(
				List<Map<String, HashMap<String, Integer>>> eventMapList) {
			// TODO Compare result and return the majority
			return null;
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
