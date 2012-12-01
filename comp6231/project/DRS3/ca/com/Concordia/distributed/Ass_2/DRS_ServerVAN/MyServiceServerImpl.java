package ca.com.Concordia.distributed.Ass_2.DRS_ServerVAN;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Hashtable;
/**
 * This class is the implemetation object for your IDL interface.
 *
 * Let the Eclipse complete operations code by choosing 'Add unimplemented methods'.
 */
public class MyServiceServerImpl extends ca.com.Concordia.distributed.Ass_2.MyServer.MyServicePOA {
	/**
	 * Constructor for MyServiceServerImpl 
	 */
	Hashtable<String, Item> itemList ;
	public MyServiceServerImpl() {
		itemList = new Hashtable<String, Item>();
		itemList.put("V00001", new Item("V00001"));
		itemList.put("V00002", new Item("V00002"));
		ServerCommunication commu = new ServerCommunication(this);
	}

	public synchronized String purchase(int customerID, String itemID, int numberOfItem) {
		Item item=new Item(itemID);
		return item.purchase(customerID, itemID, numberOfItem);
	}

	public synchronized String giveback(int customerID, String itemID, int numberOfItem) {
		Item item = new Item(itemID);
		return item.giveback(customerID, itemID, numberOfItem);
	}

	public synchronized int checkStock(String itemID) {
		Item item = new Item(itemID);
		return item.getAvailability();
	}

	public boolean haveThisItem(String itemID) {
		return itemList.containsKey(itemID);
	}

	public synchronized String exChange(String itemID1, String itemID2, int customerID, int numberOfItem) {
			Item item1 = new Item(itemID1);
			Item item2 = new Item(itemID2);
			String res=item1.isPurchased(customerID);
			if (res!=null) 
			{
				if(Integer.parseInt(res.split(" ")[2]) < numberOfItem)
				{
					return"You don't have that much items";
				}
				else
				{
					
				    if (haveThisItem(itemID2)) {
						String purchaseRes = item2.purchase(customerID, itemID2,numberOfItem);
						System.out.println(purchaseRes);
						if (!purchaseRes.equals("Not enough avalability")) {
							String returnRes = item1.giveback(customerID, itemID1, numberOfItem);
							return "Exchang success!";
						} else
							return "Not enough availability! Exchange failed";
					}
				    else
				    {
				    	try {
				    		String result = callOtherServer(itemID2 + Integer.toString(customerID)+Integer.toString(numberOfItem)).trim();
							if(result.equals("Purchase Success!"))
							{
								item1.giveback(customerID, itemID1, numberOfItem);
								return "Exchange success!";
							}
							else
								return "Not enough availability! Exchange failed";

						} catch (Exception e) {
							return e.toString();
						}
				    }
			    }
		   	}
			else
				return"You have no purchase! Exchange failed";
		}
	
	public String callOtherServer(String input) throws Exception
	{
		DatagramSocket clientSocket=new DatagramSocket();
		InetAddress IPAddress=InetAddress.getByName("localhost");
		byte[] sendData=new byte[1024];
		byte[] receiveData=new byte[1024];
		sendData=input.getBytes();
		DatagramPacket sendPacket=new DatagramPacket(sendData,sendData.length,IPAddress,6789);
		clientSocket.send(sendPacket);
		DatagramPacket receivePacket=new DatagramPacket(receiveData,receiveData.length);
		clientSocket.receive(receivePacket);
		
		String res=new String(receivePacket.getData());
		return res;
	}
}
