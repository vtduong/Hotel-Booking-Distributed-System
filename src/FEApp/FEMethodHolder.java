package FEApp;

/**
* FEApp/FEMethodHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from /Users/sandeepchowdaryannabathuni/eclipse-workspace/distributed_project/src/FEidl.idl
* Thursday, July 18, 2019 5:43:48 o'clock PM EDT
*/

public final class FEMethodHolder implements org.omg.CORBA.portable.Streamable
{
  public FEApp.FEMethod value = null;

  public FEMethodHolder ()
  {
  }

  public FEMethodHolder (FEApp.FEMethod initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = FEApp.FEMethodHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    FEApp.FEMethodHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return FEApp.FEMethodHelper.type ();
  }

}
