package vspackage.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import vspackage.RemoteMethodApp.RemoteMethodHelper;
import vspackage.config.Config;

import vspackage.tools.Logger;

public class OTW {
	
	// volatile because of thread safe
	protected static volatile Map<String, HashMap<String, Integer>> eventMap = null;
	
	/**
	 * a mapping of event and list of customers, used to keep track events a customer reserves
	 */
	protected static volatile Map<String,HashMap<String, List<String>>> eventCus = null;
	
	public static void main(String[] args) throws NumberFormatException, IOException {
		Logger logger = new Logger("OTW");
		
	    
	      try {
				// create and initialize the ORB //
				ORB orb = ORB.init(args, null);
				
				// get reference to rootpoa &amp; activate
				POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
				rootpoa.the_POAManager().activate();

				// create servant and register it with the ORB
				MethodImpl methodObj = new MethodImpl(OTW.class.getSimpleName(), OTW.class.getName());
				

				// get object reference from the servant
				org.omg.CORBA.Object ref = rootpoa.servant_to_reference(methodObj);
				
				
				// and cast the reference to a CORBA reference
				vspackage.RemoteMethodApp.RemoteMethod href = RemoteMethodHelper.narrow(ref);

				// get the root naming context
				// NameService invokes the transient name service
				org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
				
				// Use NamingContextExt, which is part of the
				// Interoperable Naming Service (INS) specification.
				NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

				// bind the Object Reference in Naming
				NameComponent path[] = ncRef.to_name("OTW");
				ncRef.rebind(path, href);

				System.out.println("Ottawa Server ready and waiting ...");
				logger.log(2, "server started registry");
				// wait for invocations from clients
				for (;;) {
					orb.run();
				}
			}

			catch (Exception e) {
				System.err.println("ERROR: " + e);
				e.printStackTrace(System.out);
				
		        System.out.println("Exception in OttwaServer.main: " + e);
		        logger.log(0, "Exception in OttawaServer.main: " + e);
			}

			

		}

}
