/**
 * 
 */
package vspackage.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import com.google.gson.Gson;

import vspackage.bean.Header;

/**
 * @author vanduong
 *
 */
public class Util {
	
	// This method starts a RMI registry on the local host, if it
	   // does not already exists at the specified port number.
	   protected static void startRegistry(int RMIPortNum) throws RemoteException{
	      try {
	         Registry registry = LocateRegistry.getRegistry(RMIPortNum);
	         registry.list( );  // This call will throw an exception
	                            // if the registry does not already exist
	      }
	      catch (RemoteException e) { 
	         // No valid registry at that port.
	/**/     System.out.println
	/**/        ("RMI registry cannot be located at port " 
	/**/        + RMIPortNum);
	         Registry registry = 
	            LocateRegistry.createRegistry(RMIPortNum);
	/**/        System.out.println(
	/**/           "RMI registry created at port " + RMIPortNum);
	      }
	   } // end startRegistry
	   
	   
	// This method lists the names registered with a Registry object
	   protected static void listRegistry(String registryURL) throws RemoteException, MalformedURLException {
	        System.out.println("Registry " + registryURL + " contains: ");
	        String [ ] names = Naming.list(registryURL);
	        for (int i=0; i < names.length; i++)
	           System.out.println(names[i]);
	   } //end listRegistry
	   
	   /**
   	 * Unicast one way, an socket bound to random port is created, the socket is closed before exit.
   	 *
   	 * @param receiverAddr the receiver addr
   	 * @param receiverPort the receiver port
   	 * @param header the header to be sent to receiver
   	 * @throws IOException Signals that an I/O exception has occurred.
   	 */
   	public static void unicastOneWay( String receiverAddr, int receiverPort, Header header) throws IOException {
		   	DatagramSocket socket = new DatagramSocket();
			Gson gson = new Gson();
			
			String data = gson.toJson(header);
			
			byte[] msg = data.getBytes();
			
			DatagramPacket packet = new DatagramPacket(msg, msg.length, 
					InetAddress.getByName(receiverAddr), receiverPort);
			socket.send(packet);
			socket.disconnect();
			socket.close();
		}
	   
	   /**
   	 * Unicast two ways: sender sends a header AND wait for response from receiver.
   	 * The method returns header response
   	 *
   	 * @param senderPort the sender port
   	 * @param receiverAddr the receiver addr
   	 * @param receiverPort the receiver port
   	 * @param header the header
   	 * @return the header
   	 * @throws NumberFormatException the number format exception
   	 * @throws IOException Signals that an I/O exception has occurred.
   	 */
   	public Header unicastTwoWays(int senderPort, String receiverAddr, int receiverPort, Header header) throws NumberFormatException, IOException{
		   	DatagramSocket socket = new DatagramSocket(senderPort);
		   	Gson gson = new Gson();
			
			String data = gson.toJson(header);
			
			byte[] msg = data.getBytes();
			
			DatagramPacket packet = new DatagramPacket(msg, msg.length, 
					InetAddress.getByName(receiverAddr), receiverPort);
			socket.send(packet);
			
			byte [] buffer = new byte[1000];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
			
			socket.receive(reply);
			socket.disconnect();
			socket.close();
			String content = new String(reply.getData());
			return  gson.fromJson(content, Header.class);

		}
}
