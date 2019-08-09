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

import com.google.gson.Gson;

import aspackage.clientServer.Util;
import ipconfig.IPConfig;
import vspackage.bean.Header;
import vspackage.tools.JSONParser;

public class Sequencer {
	private static SendRequest msr;
	private static int seqClock = 0;
	private static Map<String, String> msgqueue;
	static HashMap<String, Integer> serverports = new HashMap<String, Integer>();

	private static Sequencer seq;

	private Sequencer() throws NumberFormatException, IOException {
		msgqueue = new HashMap<String, String>();
		serverports.put(Util.MTL, 4321);
		serverports.put(Util.TOR, 4322);
		serverports.put(Util.OTW, 4323);
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
				System.out.println("Waiting for Client Request...");
				byte[] buffer = new byte[Util.BUFFER_SIZE];
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				socket.receive(request);
				msr = new SendRequest(socket, request, seqClock);
				msr.sendpacket();
				System.out.print("send reqeuest");
				seqClock++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (socket != null)
				socket.close();
		}

	}

	static class SendRequest {
		DatagramSocket socket = null;
		DatagramPacket request = null;
		int seqClock;

		public SendRequest(DatagramSocket socket, DatagramPacket request, int seqClock) {
			this.socket = socket;
			this.request = request;
			this.seqClock = seqClock + 1;
		}

		public synchronized void sendUDPMessage(String message, String ipAddress, int port) throws IOException {
			DatagramSocket socket = new DatagramSocket();
			InetAddress group = InetAddress.getByName(ipAddress);
			byte[] msg = message.getBytes();
			DatagramPacket packet = new DatagramPacket(msg, msg.length, group, port);
			socket.send(packet);
			socket.close();
		}

		public void sendpacket() {
			ExecutorService executor = Executors.newCachedThreadPool();
			executor.execute(new Runnable() {

				@Override
				public void run() {

					String content = new String(request.getData());
					JSONParser parser = new JSONParser(content);
					System.out.print("Request: " + content);
					Map<String, String> jsonObj = parser.deSerialize();
					Header header = new Header();
					header.setEventID(jsonObj.get("eventID"));
					header.setEventType(jsonObj.get("eventType"));
					header.setFromServer(jsonObj.get("fromServer"));
					header.setToServer(jsonObj.get("toServer"));
					header.setUserID(jsonObj.get("userID"));
					header.setNewEventID(jsonObj.get("newEventID"));
					header.setNewEventType(jsonObj.get("newEventType"));
					header.setProtocol(Integer.parseInt(jsonObj.get("protocol_type")));
					String capacity = jsonObj.get("capacity").trim();
					header.setCapacity(Integer.parseInt(capacity));
					System.out.println("capacity" + jsonObj.get("capacity"));
					header.setSequenceId(seqClock);
					Gson gson = new Gson();
					String data = gson.toJson(header);
					int port = 0;
					if (content.contains("userID")) {
						if (jsonObj.get("userID") != null) {
							port = serverports.get(jsonObj.get("userID").substring(0, 3));
						} else {
							port = serverports.get(jsonObj.get("fromServer"));
						}
					} else {
						port = serverports.get(jsonObj.get("fromServer").substring(0, 3));
					}
					try {
						sendUDPMessage(data, "239.0.0.0", port);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});

			executor.shutdown(); 
		}

	}

}
