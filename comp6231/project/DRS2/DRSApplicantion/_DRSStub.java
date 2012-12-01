package DRSApplicantion;


/**
* DRSApplicantion/_DRSStub.java .
* 由IDL-to-Java 编译器 (可移植), 版本 "3.2"生成
* 从G:/code/DRS-CORBA/simple.idl
* 2012年10月27日 星期六 下午11时52分50秒 EDT
*/

public class _DRSStub extends org.omg.CORBA.portable.ObjectImpl implements DRSApplicantion.DRS
{

  public String buy (String customerID, String itemID, short numberOfItem)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("buy", true);
                $out.write_string (customerID);
                $out.write_string (itemID);
                $out.write_short (numberOfItem);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return buy (customerID, itemID, numberOfItem        );
            } finally {
                _releaseReply ($in);
            }
  } // buy

  public String _return (String customerID, String itemID, short numberOfItem)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("return", true);
                $out.write_string (customerID);
                $out.write_string (itemID);
                $out.write_short (numberOfItem);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return _return (customerID, itemID, numberOfItem        );
            } finally {
                _releaseReply ($in);
            }
  } // _return

  public String checkStock (String itemID, org.omg.CORBA.StringHolder storeName, org.omg.CORBA.ShortHolder numberOfItem)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("checkStock", true);
                $out.write_string (itemID);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                storeName.value = $in.read_string ();
                numberOfItem.value = $in.read_short ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return checkStock (itemID, storeName, numberOfItem        );
            } finally {
                _releaseReply ($in);
            }
  } // checkStock

  public String exchange (String customerID, String boughtItemID, short boughtNumber, String desiredItemID, short desiredNumber)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("exchange", true);
                $out.write_string (customerID);
                $out.write_string (boughtItemID);
                $out.write_short (boughtNumber);
                $out.write_string (desiredItemID);
                $out.write_short (desiredNumber);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return exchange (customerID, boughtItemID, boughtNumber, desiredItemID, desiredNumber        );
            } finally {
                _releaseReply ($in);
            }
  } // exchange

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:DRSApplicantion/DRS:1.0"};

  public String[] _ids ()
  {
    return (String[])__ids.clone ();
  }

  private void readObject (java.io.ObjectInputStream s) throws java.io.IOException
  {
     String str = s.readUTF ();
     String[] args = null;
     java.util.Properties props = null;
     org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init (args, props);
   try {
     org.omg.CORBA.Object obj = orb.string_to_object (str);
     org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl) obj)._get_delegate ();
     _set_delegate (delegate);
   } finally {
     orb.destroy() ;
   }
  }

  private void writeObject (java.io.ObjectOutputStream s) throws java.io.IOException
  {
     String[] args = null;
     java.util.Properties props = null;
     org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init (args, props);
   try {
     String str = orb.object_to_string (this);
     s.writeUTF (str);
   } finally {
     orb.destroy() ;
   }
  }
} // class _DRSStub
