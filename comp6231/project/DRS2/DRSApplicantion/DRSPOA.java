package DRSApplicantion;


/**
* DRSApplicantion/DRSPOA.java .
* 由IDL-to-Java 编译器 (可移植), 版本 "3.2"生成
* 从G:/code/DRS-CORBA/simple.idl
* 2012年10月27日 星期六 下午11时52分50秒 EDT
*/

public abstract class DRSPOA extends org.omg.PortableServer.Servant
 implements DRSApplicantion.DRSOperations, org.omg.CORBA.portable.InvokeHandler
{

  // Constructors

  private static java.util.Hashtable _methods = new java.util.Hashtable ();
  static
  {
    _methods.put ("buy", new java.lang.Integer (0));
    _methods.put ("return", new java.lang.Integer (1));
    _methods.put ("checkStock", new java.lang.Integer (2));
    _methods.put ("exchange", new java.lang.Integer (3));
  }

  public org.omg.CORBA.portable.OutputStream _invoke (String $method,
                                org.omg.CORBA.portable.InputStream in,
                                org.omg.CORBA.portable.ResponseHandler $rh)
  {
    org.omg.CORBA.portable.OutputStream out = null;
    java.lang.Integer __method = (java.lang.Integer)_methods.get ($method);
    if (__method == null)
      throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);

    switch (__method.intValue ())
    {
       case 0:  // DRSApplicantion/DRS/buy
       {
         String customerID = in.read_string ();
         String itemID = in.read_string ();
         short numberOfItem = in.read_short ();
         String $result = null;
         $result = this.buy (customerID, itemID, numberOfItem);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 1:  // DRSApplicantion/DRS/_return
       {
         String customerID = in.read_string ();
         String itemID = in.read_string ();
         short numberOfItem = in.read_short ();
         String $result = null;
         $result = this._return (customerID, itemID, numberOfItem);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 2:  // DRSApplicantion/DRS/checkStock
       {
         String itemID = in.read_string ();
         org.omg.CORBA.StringHolder storeName = new org.omg.CORBA.StringHolder ();
         org.omg.CORBA.ShortHolder numberOfItem = new org.omg.CORBA.ShortHolder ();
         String $result = null;
         $result = this.checkStock (itemID, storeName, numberOfItem);
         out = $rh.createReply();
         out.write_string ($result);
         out.write_string (storeName.value);
         out.write_short (numberOfItem.value);
         break;
       }

       case 3:  // DRSApplicantion/DRS/exchange
       {
         String customerID = in.read_string ();
         String boughtItemID = in.read_string ();
         short boughtNumber = in.read_short ();
         String desiredItemID = in.read_string ();
         short desiredNumber = in.read_short ();
         String $result = null;
         $result = this.exchange (customerID, boughtItemID, boughtNumber, desiredItemID, desiredNumber);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       default:
         throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    }

    return out;
  } // _invoke

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:DRSApplicantion/DRS:1.0"};

  public String[] _all_interfaces (org.omg.PortableServer.POA poa, byte[] objectId)
  {
    return (String[])__ids.clone ();
  }

  public DRS _this() 
  {
    return DRSHelper.narrow(
    super._this_object());
  }

  public DRS _this(org.omg.CORBA.ORB orb) 
  {
    return DRSHelper.narrow(
    super._this_object(orb));
  }


} // class DRSPOA
