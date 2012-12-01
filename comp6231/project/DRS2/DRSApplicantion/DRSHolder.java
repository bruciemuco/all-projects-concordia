package DRSApplicantion;

/**
* DRSApplicantion/DRSHolder.java .
* 由IDL-to-Java 编译器 (可移植), 版本 "3.2"生成
* 从G:/code/DRS-CORBA/simple.idl
* 2012年10月27日 星期六 下午11时52分50秒 EDT
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
