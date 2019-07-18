package aspackage.clientServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import aspackage.OperationsApp.*;
import aspackage.beans.EventInformation;
import aspackage.utility.FileLogger;

public class OTWServer {
    public static OTW exportedObj;
    
    public static void main(String args[]){
		try {
			ORB orb = ORB.init(args, null);
			POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
			rootpoa.the_POAManager().activate();
			exportedObj = new OTW();
			exportedObj.setORB(orb);

			org.omg.CORBA.Object ref = rootpoa.servant_to_reference(exportedObj);
			DEMSOperations href = DEMSOperationsHelper.narrow(ref);

		
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

			// bind the Object Reference in Naming
			NameComponent path[] = ncRef.to_name("OTW");
			ncRef.rebind(path, href);

			System.out.println("OTW Server ready and waiting ...");
			listenUDP();

			// wait for invocations from clients
			for (;;) {
				orb.run();
			}
			
		}

		catch (Exception e) {
			System.err.println("ERROR: " + e);
			e.printStackTrace(System.out);
		}

		System.out.println("OTW Exiting ...");

	}

	
    private static String parseRequest(String input) {
		String toReturn = input;
		if (input.contains(Util.BOOK_EVENT) || input.contains(Util.Get_Booking_Schedule)
				|| input.contains(Util.CANCEL_EVENT) || input.contains(Util.List_Event_Availability)
				|| input.contains(Util.Booking_Exist) || input.contains(Util.Capasity_Exist)
				|| input.contains(Util.Can_Book) || input.contains(Util.RE)

		) {
			String[] inputArray = input.split(Util.SEMI_COLON);
			switch (inputArray[0].trim()) {
			case Util.BOOK_EVENT:
				toReturn = exportedObj.bookEvent(inputArray[1].trim(), inputArray[2].trim(), inputArray[3].trim());
				System.out.println(toReturn);
				break;
			case Util.CANCEL_EVENT:
				toReturn = exportedObj.cancelEvent(inputArray[1].trim(), inputArray[2].trim(), inputArray[3].trim());
				break;
			case Util.Get_Booking_Schedule:
				toReturn = exportedObj.udpCallforGetSchedule(inputArray[1].trim());
				System.out.println(toReturn);
				break;
			case Util.List_Event_Availability:
				toReturn = getfreeEvents(inputArray[1].trim());
				break;
			case Util.Booking_Exist:
				toReturn = exportedObj.booking_exist(inputArray[3].trim(), inputArray[1].trim(), inputArray[2].trim());
				break;
			case Util.Capasity_Exist:
				toReturn = exportedObj.capasity_exist(inputArray[1].trim(), inputArray[2].trim());
				break;
			case Util.Can_Book:
				toReturn = exportedObj.get_Customerbook(inputArray[1].trim(), inputArray[2].trim());
				
				break;
				
			case Util.RE:
				toReturn="success";
				exportedObj.removeFromCustBook(inputArray[1].trim(), inputArray[2].trim());
				break;

			default:
				break;

			}
		}
		return toReturn;

	}

	private static  String getfreeEvents(String inputArray) {
		ArrayList<HashMap<String, EventInformation>> templist = new ArrayList<HashMap<String, EventInformation>>();
		ArrayList<String> listofEvents =new ArrayList<String>();
		HashMap<String, EventInformation> tempmap = new HashMap<String, EventInformation>();
		if(exportedObj.eventBook.containsKey(inputArray)) {
			templist = exportedObj.eventBook.get(inputArray);
			for(HashMap<String, EventInformation> m :templist) {
				for(String s:m.keySet()) {
					if(m.get(s).getCapasity()!=0) {
						listofEvents.add(s +" "+ m.get(s).getCapasity());
					}
				}
				
			}
		}
		if(listofEvents.isEmpty()) {
			listofEvents.add("No events available in "+Util.OTWCITY+" city.");
		}
		return listofEvents.toString();
	}
	
    private static void listenUDP() {
		DatagramSocket aSocketTOR = null;
		String requestMsg ="";
		try {
			aSocketTOR = new DatagramSocket(2003);
			System.out.println("UDP OTW Server:");
			byte[] buffer = new byte[Util.BUFFER_SIZE];
			while (true) {
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocketTOR.receive(request);
				
				System.out.println("Request received on OTW Server: " + new String(request.getData()));
				requestMsg = new String(request.getData());
				
					String replyStr =parseRequest(requestMsg);
					
					buffer = new byte[Util.BUFFER_SIZE];
					byte[] replyBuff = new byte[Util.BUFFER_SIZE];
					replyBuff =replyStr.getBytes();
					DatagramPacket reply = new DatagramPacket(replyBuff, replyStr.length(), request.getAddress(),
							request.getPort());
					aSocketTOR.send(reply);
			}
			
			
		}catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		} finally {
			if (aSocketTOR != null)
				aSocketTOR.close();
		}
		
		
	}
    
}