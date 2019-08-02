package sequencer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;

import aspackage.clientServer.Util;
import extension.AdditionalFunctions;
import extension.Clock;
import ipconfig.IPConfig;
import vspackage.bean.Header;
import vspackage.tools.JSONParser;

public class Sequencer extends AdditionalFunctions {
	private static SendRequest msr;
    private static int seqClock = 0;
    private static Map<String,JSONObject> msgqueue;
    static ArrayList<Integer> serverPorts;
    private static Sequencer seq;
    
    private Sequencer() throws NumberFormatException, IOException {
    	super(Sequencer.class.getSimpleName());
      msgqueue = new HashMap<String, JSONObject>();
      serverPorts.add(Integer.parseInt(IPConfig.getProperty("TORPort")));
      serverPorts.add(Integer.parseInt(IPConfig.getProperty("MTLPort")));
      serverPorts.add(Integer.parseInt(IPConfig.getProperty("OTWPort")));
    	UDPListener();
    }

	public static void main(String[] args) throws NumberFormatException, IOException {
		System.out.println("Sequencer Ready And Waiting ...");
		new Sequencer();

	}

	private static void UDPListener() {
		DatagramSocket socket = null;
		try {
			int seqport = Integer.parseInt(IPConfig.getProperty("sequencer_receive_port"));
			socket = new DatagramSocket(seqport);
			while (true) {
				byte[] buffer = new byte[Util.BUFFER_SIZE];
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				socket.receive(request);
				String content = new String(request.getData());
				// update local time
				if(content.contains("resend")) {
					JSONParser parser = new JSONParser(content);
					Map<String, String> jsonObj = parser.deSerialize();
					Integer seq =Integer.parseInt(jsonObj.get("sequenceId"));
					
					
				}else {
					msr = new SendRequest(socket, request, seqClock);
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

	static class SendRequest implements Runnable {
		DatagramSocket socket = null;
		DatagramPacket request = null;
		int seqClock;

		public SendRequest(DatagramSocket socket, DatagramPacket request, int seqClock) {
			this.socket = socket;
			this.request = request;
			this.seqClock = seqClock + 1;
		}

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

			byte[] packet_to_send = json.getBytes();

			for (int i = 0; i < 4; i++) {
				try {
					InetAddress hostIP = InetAddress.getByName(IPConfig.getProperty("host" + i + 1));
					for (Integer ports : serverPorts) {
						DatagramPacket sendReq = new DatagramPacket(packet_to_send, packet_to_send.length, hostIP,
								ports);

						socket.send(sendReq);
						seq.incrementLocalTime("SE");
					}
					 msgqueue.put(String.valueOf(seqClock), jsonData);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}

	}

}
