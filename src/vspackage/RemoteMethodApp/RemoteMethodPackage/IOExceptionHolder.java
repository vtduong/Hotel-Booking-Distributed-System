package vspackage.RemoteMethodApp.RemoteMethodPackage;

/**
* RemoteMethodApp/RemoteMethodPackage/IOExceptionHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from C:/Users/vanduong/Desktop/Concordia University/Courses/Distributed Systems/workspace/Corba/src/RemoteMethod.idl
* Thursday, July 11, 2019 3:51:30 PM EDT
*/

public final class IOExceptionHolder implements org.omg.CORBA.portable.Streamable
{
  public vspackage.RemoteMethodApp.RemoteMethodPackage.IOException value = null;

  public IOExceptionHolder ()
  {
  }

  public IOExceptionHolder (vspackage.RemoteMethodApp.RemoteMethodPackage.IOException initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = vspackage.RemoteMethodApp.RemoteMethodPackage.IOExceptionHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
	  vspackage.RemoteMethodApp.RemoteMethodPackage.IOExceptionHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return vspackage.RemoteMethodApp.RemoteMethodPackage.IOExceptionHelper.type ();
  }

}
