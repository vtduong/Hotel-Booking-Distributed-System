package vspackage.RemoteMethodApp.RemoteMethodPackage;


/**
* RemoteMethodApp/RemoteMethodPackage/IOException.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from C:/Users/vanduong/Desktop/Concordia University/Courses/Distributed Systems/workspace/Corba/src/RemoteMethod.idl
* Thursday, July 11, 2019 3:51:30 PM EDT
*/

public final class IOException extends org.omg.CORBA.UserException
{
  public String exceptReason = null;

  public IOException ()
  {
    super(IOExceptionHelper.id());
  } // ctor

  public IOException (String _exceptReason)
  {
    super(IOExceptionHelper.id());
    exceptReason = _exceptReason;
  } // ctor


  public IOException (String $reason, String _exceptReason)
  {
    super(IOExceptionHelper.id() + "  " + $reason);
    exceptReason = _exceptReason;
  } // ctor

} // class IOException