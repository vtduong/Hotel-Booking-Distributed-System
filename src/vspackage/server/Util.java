/**
 * 
 */
package vspackage.server;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

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
}
