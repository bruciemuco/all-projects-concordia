package DRSApplicantion;

/**
* DRSApplicantion/DRSHolder.java .
* ��IDL-to-Java ������ (����ֲ), �汾 "3.2"����
* ��G:/code/DRS-CORBA/simple.idl
* 2012��10��27�� ������ ����11ʱ52��50�� EDT
*/

public final class DRSHolder implements org.omg.CORBA.portable.Streamable
{
  public DRSApplicantion.DRS value = null;

  public DRSHolder ()
  {
  }

  public DRSHolder (DRSApplicantion.DRS initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = DRSApplicantion.DRSHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    DRSApplicantion.DRSHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return DRSApplicantion.DRSHelper.type ();
  }

}
