package DRSApplicantion;


/**
* DRSApplicantion/DRSOperations.java .
* ��IDL-to-Java ������ (����ֲ), �汾 "3.2"����
* ��G:/code/DRS-CORBA/simple.idl
* 2012��10��27�� ������ ����11ʱ52��51�� EDT
*/

public interface DRSOperations 
{
  String buy (String customerID, String itemID, short numberOfItem);
  String _return (String customerID, String itemID, short numberOfItem);
  String checkStock (String itemID, org.omg.CORBA.StringHolder storeName, org.omg.CORBA.ShortHolder numberOfItem);
  String exchange (String customerID, String boughtItemID, short boughtNumber, String desiredItemID, short desiredNumber);
} // interface DRSOperations
