package vspackage.RemoteMethodApp.RemoteMethodPackage;

/**
* RemoteMethodApp/RemoteMethodPackage/RemoteExceptionHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from C:/Users/vanduong/Desktop/Concordia University/Courses/Distributed Systems/workspace/Corba/src/RemoteMethod.idl
* Thursday, July 11, 2019 3:51:30 PM EDT
*/

public final class RemoteExceptionHolder implements org.omg.CORBA.portable.Streamable
{
  public vspackage.RemoteMethodApp.RemoteMethodPackage.RemoteException value = null;

  public RemoteExceptionHolder ()
  {
  }

  public RemoteExceptionHolder (vspackage.RemoteMethodApp.RemoteMethodPackage.RemoteException initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = vspackage.RemoteMethodApp.RemoteMethodPackage.RemoteExceptionHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
	  vspackage.RemoteMethodApp.RemoteMethodPackage.RemoteExceptionHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return vspackage.RemoteMethodApp.RemoteMethodPackage.RemoteExceptionHelper.type ();
  }

}
