package vspackage.RemoteMethodApp.RemoteMethodPackage;


/**
* RemoteMethodApp/RemoteMethodPackage/RemoteException.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from C:/Users/vanduong/Desktop/Concordia University/Courses/Distributed Systems/workspace/Corba/src/RemoteMethod.idl
* Thursday, July 11, 2019 3:51:30 PM EDT
*/

public final class RemoteException extends org.omg.CORBA.UserException
{
  public String exceptReason = null;

  public RemoteException ()
  {
    super(RemoteExceptionHelper.id());
  } // ctor

  public RemoteException (String _exceptReason)
  {
    super(RemoteExceptionHelper.id());
    exceptReason = _exceptReason;
  } // ctor


  public RemoteException (String $reason, String _exceptReason)
  {
    super(RemoteExceptionHelper.id() + "  " + $reason);
    exceptReason = _exceptReason;
  } // ctor

} // class RemoteException