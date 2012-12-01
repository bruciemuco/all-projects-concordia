package DRSApplicantion;


/**
* DRSApplicantion/DRSOperations.java .
* 由IDL-to-Java 编译器 (可移植), 版本 "3.2"生成
* 从G:/code/DRS-CORBA/simple.idl
* 2012年10月27日 星期六 下午11时52分51秒 EDT
*/

public interface DRSOperations 
{
  String buy (String customerID, String itemID, short numberOfItem);
  String _return (String customerID, String itemID, short numberOfItem);
  String checkStock (String itemID, org.omg.CORBA.StringHolder storeName, org.omg.CORBA.ShortHolder numberOfItem);
  String exchange (String customerID, String boughtItemID, short boughtNumber, String desiredItemID, short desiredNumber);
} // interface DRSOperations
