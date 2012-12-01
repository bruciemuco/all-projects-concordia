package DRSApplicantion;


/**
* DRSApplicantion/DRSHelper.java .
* 由IDL-to-Java 编译器 (可移植), 版本 "3.2"生成
* 从G:/code/DRS-CORBA/simple.idl
* 2012年10月27日 星期六 下午11时52分50秒 EDT
*/

abstract public class DRSHelper
{
  private static String  _id = "IDL:DRSApplicantion/DRS:1.0";

  public static void insert (org.omg.CORBA.Any a, DRSApplicantion.DRS that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static DRSApplicantion.DRS extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      __typeCode = org.omg.CORBA.ORB.init ().create_interface_tc (DRSApplicantion.DRSHelper.id (), "DRS");
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static DRSApplicantion.DRS read (org.omg.CORBA.portable.InputStream istream)
  {
    return narrow (istream.read_Object (_DRSStub.class));
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, DRSApplicantion.DRS value)
  {
    ostream.write_Object ((org.omg.CORBA.Object) value);
  }

  public static DRSApplicantion.DRS narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof DRSApplicantion.DRS)
      return (DRSApplicantion.DRS)obj;
    else if (!obj._is_a (id ()))
      throw new org.omg.CORBA.BAD_PARAM ();
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      DRSApplicantion._DRSStub stub = new DRSApplicantion._DRSStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

  public static DRSApplicantion.DRS unchecked_narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof DRSApplicantion.DRS)
      return (DRSApplicantion.DRS)obj;
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      DRSApplicantion._DRSStub stub = new DRSApplicantion._DRSStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

}
