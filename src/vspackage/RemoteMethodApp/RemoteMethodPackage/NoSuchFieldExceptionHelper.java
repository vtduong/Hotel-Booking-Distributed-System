package vspackage.RemoteMethodApp.RemoteMethodPackage;


/**
* RemoteMethodApp/RemoteMethodPackage/NoSuchFieldExceptionHelper.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from C:/Users/vanduong/Desktop/Concordia University/Courses/Distributed Systems/workspace/Corba/src/RemoteMethod.idl
* Thursday, July 11, 2019 3:51:30 PM EDT
*/

abstract public class NoSuchFieldExceptionHelper
{
  private static String  _id = "IDL:RemoteMethodApp/RemoteMethod/NoSuchFieldException:1.0";

  public static void insert (org.omg.CORBA.Any a, vspackage.RemoteMethodApp.RemoteMethodPackage.NoSuchFieldException that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static vspackage.RemoteMethodApp.RemoteMethodPackage.NoSuchFieldException extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  private static boolean __active = false;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      synchronized (org.omg.CORBA.TypeCode.class)
      {
        if (__typeCode == null)
        {
          if (__active)
          {
            return org.omg.CORBA.ORB.init().create_recursive_tc ( _id );
          }
          __active = true;
          org.omg.CORBA.StructMember[] _members0 = new org.omg.CORBA.StructMember [1];
          org.omg.CORBA.TypeCode _tcOf_members0 = null;
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_string_tc (0);
          _members0[0] = new org.omg.CORBA.StructMember (
            "exceptReason",
            _tcOf_members0,
            null);
          __typeCode = org.omg.CORBA.ORB.init ().create_exception_tc (vspackage.RemoteMethodApp.RemoteMethodPackage.NoSuchFieldExceptionHelper.id (), "NoSuchFieldException", _members0);
          __active = false;
        }
      }
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static vspackage.RemoteMethodApp.RemoteMethodPackage.NoSuchFieldException read (org.omg.CORBA.portable.InputStream istream)
  {
	vspackage.RemoteMethodApp.RemoteMethodPackage.NoSuchFieldException value = new vspackage.RemoteMethodApp.RemoteMethodPackage.NoSuchFieldException ();
    // read and discard the repository ID
    istream.read_string ();
    value.exceptReason = istream.read_string ();
    return value;
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, vspackage.RemoteMethodApp.RemoteMethodPackage.NoSuchFieldException value)
  {
    // write the repository ID
    ostream.write_string (id ());
    ostream.write_string (value.exceptReason);
  }

}