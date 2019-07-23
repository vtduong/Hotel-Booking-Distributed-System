package vspackage.server;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import extension.AdditionalFunctions;
import vspackage.RemoteMethodApp.RemoteMethodHelper;
import vspackage.RemoteMethodApp.RemoteMethodPackage.ClassNotFoundException;
import vspackage.RemoteMethodApp.RemoteMethodPackage.IllegalArgumentException;
import vspackage.RemoteMethodApp.RemoteMethodPackage.NoSuchFieldException;
import vspackage.RemoteMethodApp.RemoteMethodPackage.SecurityException;
import vspackage.tools.Logger;

public class MTL extends MethodImpl{
	
	// volatile because of thread safe
	protected static volatile Map<String, HashMap<String, Integer>> eventMap = null;
	
	/**
	 * a mapping of event and list of customers, used to keep track events a customer reserves
	 */
	protected static volatile Map<String,HashMap<String, List<String>>> eventCus = null;
	
	
	/**
	 * Constructor
	 * @throws RemoteException
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws ClassNotFoundException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws IOException
	 */
	MTL() throws RemoteException, SecurityException, NoSuchFieldException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException, IOException{
		super(MTL.class.getSimpleName(), MTL.class.getName());
	}
	
	public static void main(String[] args) throws NumberFormatException, IOException {
		Logger logger = new Logger("MTL");
		MTL instance = null;		
	    
	      try {
	  		instance= new MTL();
	      }catch (Exception e) {
				System.err.println("ERROR: " + e);
				e.printStackTrace(System.out);
				
		        System.out.println("Exception in MontrealServer.main: " + e);
		        logger.log(0, "Exception in MontrealServer.main: " + e);
			}

		}
	}



