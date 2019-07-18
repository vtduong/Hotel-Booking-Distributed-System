package aspackage.OperationsApp;


/**
* OperationsApp/DEMSOperationsPOA.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from Operation.idl
* Wednesday, July 3, 2019 7:46:13 PM EDT
*/

public abstract class DEMSOperationsPOA extends org.omg.PortableServer.Servant
 implements aspackage.OperationsApp.DEMSOperationsOperations, org.omg.CORBA.portable.InvokeHandler
{

  // Constructors

  private static java.util.Hashtable _methods = new java.util.Hashtable ();
  static
  {
    _methods.put ("addEvent", new java.lang.Integer (0));
    _methods.put ("bookEvent", new java.lang.Integer (1));
    _methods.put ("getBookingSchedule", new java.lang.Integer (2));
    _methods.put ("cancelEvent", new java.lang.Integer (3));
    _methods.put ("removeEvent", new java.lang.Integer (4));
    _methods.put ("listEventAvailability", new java.lang.Integer (5));
    _methods.put ("swapEvent", new java.lang.Integer (6));
    _methods.put ("shutdown", new java.lang.Integer (7));
  }

  public org.omg.CORBA.portable.OutputStream _invoke (String $method,
                                org.omg.CORBA.portable.InputStream in,
                                org.omg.CORBA.portable.ResponseHandler $rh)
  {
    org.omg.CORBA.portable.OutputStream out = null;
    java.lang.Integer __method = (java.lang.Integer)_methods.get ($method);
    if (__method == null)
      throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);

    switch (__method.intValue ())
    {
       case 0:  // OperationsApp/DEMSOperations/addEvent
       {
         String eventId = in.read_string ();
         String eventType = in.read_string ();
         int bookingCapacity = in.read_long ();
         String $result = null;
         $result = this.addEvent (eventId, eventType, bookingCapacity);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 1:  // OperationsApp/DEMSOperations/bookEvent
       {
         String customerId = in.read_string ();
         String eventId = in.read_string ();
         String eventType = in.read_string ();
         String $result = null;
         $result = this.bookEvent (customerId, eventId, eventType);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 2:  // OperationsApp/DEMSOperations/getBookingSchedule
       {
         String customerId = in.read_string ();
         String $result = null;
         $result = this.getBookingSchedule (customerId);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 3:  // OperationsApp/DEMSOperations/cancelEvent
       {
         String customerId = in.read_string ();
         String eventId = in.read_string ();
         String eventType = in.read_string ();
         String $result = null;
         $result = this.cancelEvent (customerId, eventId, eventType);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 4:  // OperationsApp/DEMSOperations/removeEvent
       {
         String eventId = in.read_string ();
         String eventType = in.read_string ();
         String $result = null;
         $result = this.removeEvent (eventId, eventType);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 5:  // OperationsApp/DEMSOperations/listEventAvailability
       {
         String eventType = in.read_string ();
         String $result = null;
         $result = this.listEventAvailability (eventType);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 6:  // OperationsApp/DEMSOperations/swapEvent
       {
         String customerID = in.read_string ();
         String newEventID = in.read_string ();
         String newEventType = in.read_string ();
         String oldEventID = in.read_string ();
         String oldEventType = in.read_string ();
         String $result = null;
         $result = this.swapEvent (customerID, newEventID, newEventType, oldEventID, oldEventType);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 7:  // OperationsApp/DEMSOperations/shutdown
       {
         this.shutdown ();
         out = $rh.createReply();
         break;
       }

       default:
         throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    }

    return out;
  } // _invoke

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:OperationsApp/DEMSOperations:1.0"};

  public String[] _all_interfaces (org.omg.PortableServer.POA poa, byte[] objectId)
  {
    return (String[])__ids.clone ();
  }

  public DEMSOperations _this() 
  {
    return DEMSOperationsHelper.narrow(
    super._this_object());
  }

  public DEMSOperations _this(org.omg.CORBA.ORB orb) 
  {
    return DEMSOperationsHelper.narrow(
    super._this_object(orb));
  }


} // class DEMSOperationsPOA
