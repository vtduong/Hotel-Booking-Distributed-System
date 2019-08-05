package sequencer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.simple.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;

import aspackage.clientServer.Util;
import extension.AdditionalFunctions;
import extension.Clock;
import ipconfig.IPConfig;
import vspackage.bean.Header;
import vspackage.tools.JSONParser;

public class Sequencer{
	private static SendRequest msr;
    private static int seqClock = 0;
    private static Map<String,JSONObject> msgqueue;
    static ArrayList<Integer> serverPorts = new ArrayList<Integer>() ;
    private static Sequencer seq;
    
    private Sequencer() throws NumberFormatException, IOException {
//    	super(Sequencer.class.getSimpleName());
      msgqueue = new HashMap<String, JSONObject>();
      serverPorts.add(Integer.parseInt(IPConfig.getProperty("tor_port_as")));
      serverPorts.add(Integer.parseInt(IPConfig.getProperty("mtl_port_as")));
      serverPorts.add(Integer.parseInt(IPConfig.getProperty("otw_port_as")));
      serverPorts.add(Integer.parseInt(IPConfig.getProperty("tor_port_vs")));
      serverPorts.add(Integer.parseInt(IPConfig.getProperty("mtl_port_vs")));
      serverPorts.add(Integer.parseInt(IPConfig.getProperty("otw_port_vs")));
    	UDPListener();
    }

	public static void main(String[] args) throws NumberFormatException, IOException {
		System.out.println("Sequencer Ready And Waiting ...");
		new Sequencer();

	}

	private static void UDPListener() {
		DatagramSocket socket = null;
		try {
			int seqport = Integer.parseInt(IPConfig.getProperty("sequencer_port"));
			socket = new DatagramSocket(seqport);
			while (true) {
				byte[] buffer = new byte[Util.BUFFER_SIZE];
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				socket.receive(request);
				String content = new String(request.getData());
				if(content.contains("resend")) {
					JSONParser parser = new JSONParser(content);
					Map<String, String> jsonObj = parser.deSerialize();
					Integer seq =Integer.parseInt(jsonObj.get("sequenceId"));
					String json = msgqueue.get(seq).toJSONString();
					byte[] data = json.getBytes();
					DatagramPacket packet = new DatagramPacket(data, data.length, request.getAddress(), request.getPort());
					socket.send(packet);
					new Retry(socket, packet);
					
				}else {
					
					msr = new SendRequest(socket, request, seqClock);
					System.out.print("send reqeuest");
					seqClock++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (socket != null)
				socket.close();
		}

	}
	static class Retry implements Runnable {
		DatagramSocket socket = null;
		DatagramPacket request = null;
		public Retry(DatagramSocket socket, DatagramPacket request) {
			this.socket = socket;
			this.request = request;
			
		}

		@Override
		public void run() {
			
			try {
				socket.send(request);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	static class SendRequest extends Thread {
		DatagramSocket socket = null;
		DatagramPacket request = null; 
		int seqClock;

		public SendRequest(DatagramSocket socket, DatagramPacket request, int seqClock) {
			this.socket = socket;
			this.request = request;
			this.seqClock = seqClock + 1;
			sendpacket();
		}
		
       public void sendpacket(){
		ExecutorService executor = Executors.newCachedThreadPool();
		executor.execute(new Runnable() {
			
			@Override
			public void run() {
				ObjectMapper mapper = new ObjectMapper();
				String content = new String(request.getData());
				JSONParser parser = new JSONParser(content);
				Map<String, String> jsonObj = parser.deSerialize();
				Header data = new Header();
				data.setSequenceId(seqClock);
				JSONObject jsonData = new JSONObject();
				jsonData.put("sequenceId", data.getSequenceId());
				String json = jsonData.toJSONString();
				System.out.println("Json string"+json);
				byte[] packet_to_send = json.getBytes();

				for (int i = 0; i < 4; i++) {
					try {
						String hostname = IPConfig.getProperty("host" + i + 1);
						System.out.println(hostname);
						InetAddress hostIP = InetAddress.getByName(IPConfig.getProperty("host" + i + 1));
						for (Integer ports : serverPorts) {
							DatagramPacket sendReq = new DatagramPacket(packet_to_send, packet_to_send.length, hostIP,
									ports);

							socket.send(sendReq);
						}
						 msgqueue.put(String.valueOf(seqClock), jsonData);
					} catch (IOException e) {
						
						e.printStackTrace();
					}finally {
						
					}

				}

			}
		});
		
		executor.shutdown();
       }
		

	}

}
