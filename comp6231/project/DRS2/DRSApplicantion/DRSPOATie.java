package DRSApplicantion;


/**
* DRSApplicantion/DRSPOATie.java .
* 由IDL-to-Java 编译器 (可移植), 版本 "3.2"生成
* 从G:/code/DRS-CORBA/simple.idl
* 2012年10月27日 星期六 下午11时52分50秒 EDT
*/

public class DRSPOATie extends DRSPOA
{

  // Constructors

  public DRSPOATie ( DRSApplicantion.DRSOperations delegate ) {
      this._impl = delegate;
  }
  public DRSPOATie ( DRSApplicantion.DRSOperations delegate , org.omg.PortableServer.POA poa ) {
      this._impl = delegate;
      this._poa      = poa;
  }
  public DRSApplicantion.DRSOperations _delegate() {
      return this._impl;
  }
  public void _delegate (DRSApplicantion.DRSOperations delegate ) {
      this._impl = delegate;
  }
  public org.omg.PortableServer.POA _default_POA() {
      if(_poa != null) {
          return _poa;
      }
      else {
          return super._default_POA();
      }
  }
  public String buy (String customerID, String itemID, short numberOfItem)
  {
    return _impl.buy(customerID, itemID, numberOfItem);
  } // buy

  public String _return (String customerID, String itemID, short numberOfItem)
  {
    return _impl._return(customerID, itemID, numberOfItem);
  } // _return

  public String checkStock (String itemID, org.omg.CORBA.StringHolder storeName, org.omg.CORBA.ShortHolder numberOfItem)
  {
    return _impl.checkStock(itemID, storeName, numberOfItem);
  } // checkStock

  public String exchange (String customerID, String boughtItemID, short boughtNumber, String desiredItemID, short desiredNumber)
  {
    return _impl.exchange(customerID, boughtItemID, boughtNumber, desiredItemID, desiredNumber);
  } // exchange

  private DRSApplicantion.DRSOperations _impl;
  private org.omg.PortableServer.POA _poa;

} // class DRSPOATie
