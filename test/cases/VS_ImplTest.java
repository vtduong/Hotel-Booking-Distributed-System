/**
 * 
 */
package cases;

import static org.junit.Assert.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import vspackage.server.MTL;
import vspackage.server.OTW;
import vspackage.server.TOR;
import vspackage.RemoteMethodApp.RemoteMethodHelper;
import vspackage.RemoteMethodApp.RemoteMethodPackage.AccessDeniedException;
import vspackage.RemoteMethodApp.RemoteMethodPackage.ClassNotFoundException;
import vspackage.RemoteMethodApp.RemoteMethodPackage.IOException;
import vspackage.RemoteMethodApp.RemoteMethodPackage.RemoteException;
import vspackage.bean.Protocol;
import vspackage.client.Client;
import vspackage.server.MethodImpl;

/**
 * @author vanduong
 *
 */
public class VS_ImplTest {

//	private vspackage.RemoteMethodApp.RemoteMethod otw = null;
//	private vspackage.RemoteMethodApp.RemoteMethod mtl = null;
//	private vspackage.RemoteMethodApp.RemoteMethod tor = null;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		//TODO start servers before run tests
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		clearClassData("MTL");
		clearClassData("TOR");
		clearClassData("OTW");
	}

	@Ignore
	private void clearClassData(String name) throws NoSuchFieldException, SecurityException, java.lang.ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
		// Get the private String field
		final Field field = Class.forName(name).getDeclaredField("eventMap");
		// Allow modification on the field
		field.setAccessible(true);
		// Get
		final Object oldValue = field.get(Class.forName(name).getName());
		// Sets the field to the new value
		field.set(oldValue, null);
		
		// Get the private String field
		final Field field1 = Class.forName(name).getDeclaredField("eventCus");
		// Allow modification on the field
		field.setAccessible(true);
		// Get
		final Object oldValue2 = field.get(Class.forName(name).getName());
		// Sets the field to the new value
		field.set(oldValue, null);
		
	}

	private vspackage.RemoteMethodApp.RemoteMethod startORB(String hostName) throws InvalidName, NotFound, CannotProceed, org.omg.CosNaming.NamingContextPackage.InvalidName {
		ORB orb = ORB.init(new String[] {null}, null);
		//-ORBInitialPort 1050 -ORBInitialHost localhost
		org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
		NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
		return (vspackage.RemoteMethodApp.RemoteMethod) RemoteMethodHelper.narrow(ncRef.resolve_str(hostName));
	}

	@Test
	public void test1() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, RemoteException, AccessDeniedException, ClassNotFoundException, IOException, InvalidName, NotFound, CannotProceed, org.omg.CosNaming.NamingContextPackage.InvalidName {
		//		Method method = Client.class.getDeclaredMethod("connectServer", String.class, Integer.class, String.class);
//		method.setAccessible(true);
//		method.invoke(client, hos, Protocol.ADD_EVENT, "OTWM4560");		
//		String result = otw.addEvent("OTWE080619", "Conference", 1);
//		assertTrue(result.contains("successfully"));
//		result = otw.addEvent("OTWE110619", "Conference", 1);
//		assertTrue(result.contains("successfully"));
//		result = otw.addEvent("TORE050619", "Conference", 1);
//		assertFalse(result.contains("successfully"));
		
		
	}
	
	@Test
	public void Test() throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Constructor<MethodImpl> constructor;
        constructor = MethodImpl.class.getDeclaredConstructor(Object.class);
        constructor.setAccessible(true);
        MethodImpl mtl = constructor.newInstance("MTL");
        
	}
	
	

}
