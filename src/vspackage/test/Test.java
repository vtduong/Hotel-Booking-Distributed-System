package vspackage.test;

import java.io.IOException;
import java.util.Scanner;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import FEApp.FEMethodHelper;
import frontend.FrontEnd;
import vspackage.RemoteMethodApp.RemoteMethodHelper;
import vspackage.tools.Logger;

public class Test {
	
	
	public static final String CLIENT_MTL_ONE = "MTLC5001";
	public static final String CLIENT_MTL_TWO = "MTLC5002";
	public static final String CLIENT_MTL_THREE = "MTLC5003";
	
	public static final String CLIENT_TOR_ONE = "TORC6001";
	public static final String CLIENT_TOR_TWO = "TORC6002";
	public static final String CLIENT_TOR_THREE = "TORC6003";
	
	public static final String CLIENT_OTW_ONE = "OTWC7001";
	public static final String CLIENT_OTW_TWO = "OTWC7002";
	public static final String CLIENT_OTW_THREE = "OTWC7003";
	
	// Only two available for event type Seminar
	public static final String EVENT_ID_ONE = "TORE110619";
	
	// Only one available for event type Seminar
	public static final String EVENT_ID_TWO = "TORM121111";
	
	// Only two available for event type Seminar
	public static final String EVENT_ID_THREE = "OTWA011111";
	
	public static void main(String[] args) {
		FrontEnd h = new FrontEnd();
		// Client one
		BookAction clientOne = new BookAction(args, CLIENT_MTL_ONE, EVENT_ID_ONE ,"Seminar",h);
		
		// Client two
		BookAction clientTwo = new BookAction(args, CLIENT_OTW_TWO, EVENT_ID_ONE ,"Seminar",h);
		
		// Client three
		BookAction clientThree = new BookAction(args, CLIENT_MTL_THREE, EVENT_ID_ONE ,"Seminar",h);
		
		// Client four
		BookAction clientFour = new BookAction(args, CLIENT_TOR_ONE, EVENT_ID_TWO ,"Seminar",h);
		CancelAction clientFourCancel = new CancelAction(args, CLIENT_TOR_ONE, EVENT_ID_TWO ,"Seminar",h);
		SwapAction clientFourSwap = new SwapAction(args, CLIENT_TOR_ONE, EVENT_ID_TWO ,"Seminar", EVENT_ID_THREE ,"Seminar",h);
		
		// Booking thread for client one
		Thread bookThreadOne = new Thread(clientOne);
		
		// Booking thread for client two
		Thread bookThreadTwo = new Thread(clientTwo);
		
		// Booking thread for client three
		Thread bookThreadThree = new Thread(clientThree);
		
		// Booking thread for client four
		Thread bookThreadFour = new Thread(clientFour);
		
		// Cancel thread for client four
		Thread cancelThreadFour = new Thread(clientFourCancel);
		
		// Swap thread for client four
		Thread swapThreadFour = new Thread(clientFourSwap);
		
		bookThreadOne.start();
		bookThreadTwo.start();
		bookThreadThree.start();
		bookThreadFour.start();
		
		cancelThreadFour.start();
		swapThreadFour.start();
		
	}
}

class BookAction implements Runnable {
	
	private FrontEnd h = null;
	private String[] args = null;
	private String cusID = null;
	private String eventID = null;
	private String eventType = null;
	
	public BookAction(String[] args, String cusID, String eventID, String eventType, FrontEnd h) {
		this.args = args;
		this.cusID = cusID;
		this.eventID = eventID;
		this.eventType = eventType;
		this.h = h;
	}
	
	@Override
	public void run() {
		connectServer(args, cusID, eventID, eventType);	
		
	}
	
	private synchronized void connectServer(String[] args, String cusID, String eventID, String eventType) {
		//create a logger for this client
		Logger logger = new Logger(cusID, true);
		try {
//			ORB orb = ORB.init(new String[] {null}, null);
//			//-ORBInitialPort 1050 -ORBInitialHost localhost
//			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
//			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
//			FEApp.FEMethod h = (FEApp.FEMethod) FEMethodHelper.narrow(ncRef.resolve_str("FE"));
	         logger.log(2, "Lookup completed");
	         System.out.println("Lookup completed " );
	         
	         Scanner scan = new Scanner(System.in);
	         
	         synchronized (h) {
	        	logger.log(2, "booking an event for " + eventType + eventID);
	 	     	String status = h.bookEvent(cusID, eventID, eventType);
	 	        System.out.println(cusID + " for " + eventType + " : " + status);
	 	        logger.log(2, status);
			}
	         
	     	
	             
	      }
	      catch (Exception e) {
	         //System.out.println("Exception in Client: " + e);
	         //e.printStackTrace();
	         
	         try {
				logger.log(0, "Exception in Client: " + e.getMessage());
			} catch (IOException e1) {
				//System.out.println("Error while trying to log in Client");
				//e1.printStackTrace();
			}
	      } 
		
	}
	
}




class CancelAction implements Runnable {
	
	private FrontEnd h = null;
	private String[] args = null;
	private String cusID = null;
	private String eventID = null;
	private String eventType = null;
	
	public CancelAction(String[] args, String cusID, String eventID, String eventType, FrontEnd h) {
		this.args = args;
		this.cusID = cusID;
		this.eventID = eventID;
		this.eventType = eventType;
		this.h = h;
	}
	
	@Override
	public void run() {
		connectServer(args, cusID, eventID, eventType);	
		
	}
	
	private synchronized void connectServer(String[] args, String cusID, String eventID, String eventType) {
		//create a logger for this client
		Logger logger = new Logger(cusID, true);
		try {
//			ORB orb = ORB.init(new String[] {null}, null);
//			//-ORBInitialPort 1050 -ORBInitialHost localhost
//			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
//			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
//			FEApp.FEMethod h = (FEApp.FEMethod) FEMethodHelper.narrow(ncRef.resolve_str("FE"));
	         logger.log(2, "Lookup completed");
	         System.out.println("Lookup completed " );
	         
	         Scanner scan = new Scanner(System.in);
	       
	         
	         synchronized (h) {
	        	 logger.log(2, "Cancel an event for " + eventType + eventID);
	 	        String status = h.cancelEvent(cusID, eventID, eventType);
	 	        System.out.println(cusID + "for " + eventType + " : " + status);
	 	        logger.log(2, status);
			}
	     	
	             
	      }
	      catch (Exception e) {
	         //System.out.println("Exception in Client: " + e);
	         //e.printStackTrace();
	         
	         try {
				logger.log(0, "Exception in Client: " + e.getMessage());
			} catch (IOException e1) {
				//System.out.println("Error while trying to log in Client");
				//e1.printStackTrace();
			}
	      } 
		
	}
}

	
	class SwapAction implements Runnable {
		
		private FrontEnd h = null;
		private String[] args = null;
		private String cusID = null;
		private String oldEventID = null;
		private String oldEventType = null;
		private String newEventID = null;
		private String newEventType = null;	
		
		public SwapAction(String[] args, String cusID, String oldEventID, String oldEventType, String newEventID, String newEventType, FrontEnd h) {
			this.args = args;
			this.cusID = cusID;
			this.oldEventID = oldEventID;
			this.oldEventType = oldEventType;
			this.newEventID = newEventID;
			this.newEventType = newEventType;
			this.h = h;
		}
		
		@Override
		public void run() {
			connectServer(args, cusID, oldEventID, oldEventType, newEventID, newEventType);	
			
		}
		
		private  synchronized void connectServer(String[] args, String cusID, String oldEventID, String oldEventType, String newEventID, String newEventType) {
			//create a logger for this client
			Logger logger = new Logger(cusID, true);
			try {
//				ORB orb = ORB.init(new String[] {null}, null);
//				//-ORBInitialPort 1050 -ORBInitialHost localhost
//				org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
//				NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
//				FEApp.FEMethod h = (FEApp.FEMethod) FEMethodHelper.narrow(ncRef.resolve_str("FE"));
		         logger.log(2, "Lookup completed");
		         System.out.println("Lookup completed " );
		         
		         Scanner scan = new Scanner(System.in);
		        
		        synchronized (h) {
		        	logger.log(2, "Swap an event");
			        String status = h.swapEvent(cusID, newEventID, newEventType, oldEventID, oldEventType);
			        System.out.println(cusID + "for "  + " : " + status);
			        logger.log(2, status);
				}
		             
		      }
		      catch (Exception e) {
		         //System.out.println("Exception in Client: " + e);
		         //e.printStackTrace();
		         
		         try {
					logger.log(0, "Exception in Client: " + e.getMessage());
				} catch (IOException e1) {
					//System.out.println("Error while trying to log in Client");
					//e1.printStackTrace();
				}
		      } 
			
		}

}