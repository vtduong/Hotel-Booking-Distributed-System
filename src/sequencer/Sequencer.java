package sequencer;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Map;

import org.json.simple.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;

import aspackage.clientServer.Util;
import extension.AdditionalFunctions;
import extension.Clock;
import ipconfig.IPConfig;
import vspackage.bean.Header;
import vspackage.tools.JSONParser;

public class Sequencer  extends AdditionalFunctions{
	private static SendRequest msr;
	private static int seqClock=0;
    private static int[]serverPorts;

	public static void main(String[] args) {
		System.out.println("Sequencer Ready And Waiting ...");
		serverPorts = new int[4];
		UDPListener();
	}
	
	

	private static void UDPListener() {
		DatagramSocket socket = null;
		try{
			int seqport = Integer.parseInt(IPConfig.getProperty("sequencer_receive_port"));
			socket = new DatagramSocket(seqport);
			while(true){
				byte[] buffer = new byte[Util.BUFFER_SIZE]; 
				DatagramPacket request = new DatagramPacket(buffer, buffer.length); 
				socket.receive(request);
				// update local time
				msr = new SendRequest(socket, request,seqClock);
				seqClock++;
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			if(socket != null) socket.close();
		}
		
	}



	
	
	
	static class SendRequest implements Runnable {
		DatagramSocket socket = null;
		DatagramPacket request = null;
		int seqClock;
		public SendRequest(DatagramSocket socket, DatagramPacket request,int seqClock) {
			this.socket=socket;
			this.request =request;
			this.seqClock=seqClock+1;
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
			
			
			for(int i=0;i<4;i++) {
				// call increment local clock.
				//TODO
			}
			
			
		}
		
	}
	
	

}
