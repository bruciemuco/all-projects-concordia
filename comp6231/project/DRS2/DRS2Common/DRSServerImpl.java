package DRS2Common;
 /* 
 * COMP6231-Assignment2-CORBA
 * Author: YICHEN LI
 * Student ID: 6389635
 */ 
import org.omg.CORBA.ORB;
import org.omg.CORBA.ShortHolder;
import org.omg.CORBA.StringHolder;
import java.io.*;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


/**
 * This class is the implemetation object for your IDL interface.
 *
 * Let the Eclipse complete operations code by choosing 'Add unimplemented methods'.
 */
public class DRSServerImpl extends DRSApplicantion.DRSPOA {
	
	String fileName = "";
	
	private Hashtable<String,Item> inventory = new Hashtable<String,Item>(10);
	private Semaphore [] semaphore;
	
	//read write lock for read and write store file
	private final ReadWriteLock readwritelockStore = new ReentrantReadWriteLock();
	private final Lock readStore = readwritelockStore.readLock();
	private final Lock writeStore = readwritelockStore.writeLock();
	
	//read write lock for read and write customer file
	private final ReadWriteLock readwritelockCustomer = new ReentrantReadWriteLock();
	private final Lock writeCustomer = readwritelockCustomer.writeLock();
	private final Lock readCustomer = readwritelockCustomer.readLock();
	
	private ORB orb;
	public void setORB(ORB orb_val)
	{
		orb = orb_val;
		
	}
	
	
	
	/**
	 * Constructor for DRSServerImpl 
	 */
	public DRSServerImpl(){
		
	}
	
	String port_8888 = "28888";
	
	public DRSServerImpl(String fileName) {
		this.fileName = fileName;
		initializeMap("./DRS2/" + fileName);
		
		semaphore = new Semaphore[inventory.size()];
		//initialize semaphore to ensure each item obtain one semaphore
		for(int i=0;i<inventory.size();i++)
		{
			semaphore[i] = new Semaphore(1);
			
		}
		
		//start UDP server
		UDPServer udpserver=null;
		int switch_val = 0;
		if (fileName.equals("Montreal")) {
			switch_val = 1;
		} else if (fileName.equals("Toronto")) {
			switch_val = 2;
		} else if (fileName.equals("Vancouver")) {
			switch_val = 3;
		}
		switch(switch_val)
		{
			case 1:
				udpserver=new UDPServer(fileName,port_8888);
				break;
			case 2:
				udpserver=new UDPServer(fileName,"9999");
				break;
			case 3:
				udpserver=new UDPServer(fileName,"6790");
				break;
		}
		udpserver.start();
		
		
		
		
	}

	@Override
	public String buy(String customerID, String itemID, short numberOfItem) {
		// TODO Auto-generated method stub
		int num;
		String content="";
		if(inventory.containsKey(itemID))
		{
			//get index of itemID in hashtable
			int index=(inventory.get(itemID)).getIndex();
			try
			{
				
			
				num=(inventory.get(itemID)).getNumberOfItem();
				if(numberOfItem<=num)
				{
					semaphore[index].acquire();  //use semaphore to lock item
					
					num=num-numberOfItem;
					
					inventory.remove(itemID);
					inventory.put(itemID,new Item(itemID,num,index));
					
					updateCstFile(customerID,itemID,numberOfItem,"+");  //update customer file
					saveMapToFile(fileName);
					
					semaphore[index].release(); //release item
					
					content= "customer "+customerID+" successfully bought "+numberOfItem+ " of item "+itemID+" in "+fileName;
				}
				else
				{
					semaphore[index].acquire();  //use semaphore to lock item
					
					inventory.remove(itemID);
					content= "customer "+customerID+" successfully bought "+num+ " of item "+itemID+" in "+fileName+" ,need still to purchase "+(numberOfItem-num)+"\r\n";
					saveMapToFile(fileName);
					
					//go to next store to buy the rest itemID,numberOfItem-num
					UDPBuy udpbuy=new UDPBuy();
					String rtnmessage=udpbuy.getBuyInfo(fileName,itemID,numberOfItem-num);
					String []text=rtnmessage.split(",");
					if(text[1].equals("-1"))
					{
						content=content+" there is no more item: "+itemID+" in other two stores";
						updateCstFile(customerID,itemID,num,"+");  //update customer file
					
					}
					else
					{
						content=content+" bought "+text[1]+" of item:" +itemID+" in store "+text[0];
						updateCstFile(customerID,itemID,numberOfItem,"+");  //update customer file
					}
					
					semaphore[index].release(); //release item
				}
			}catch(InterruptedException ex) {  
				ex.printStackTrace();  
				return "error: "+ex.getMessage();
			}  


		}
		else
		{
			
			content= "customer "+customerID+" bought nothing in "+fileName+"\r\n";
			//go to next store to buy itemID, numberOfItem

			UDPBuy udpbuy=new UDPBuy();
			String rtnmessage=udpbuy.getBuyInfo(fileName,itemID,numberOfItem);
			String []text=rtnmessage.split(",");
			if(text[1].equals("-1"))
			{
				content="item "+itemID+" is not available in any of the stores";
			}
			else
			{
				content=content+" bought "+text[1]+" of item:" +itemID+" in store "+text[0];
				updateCstFile(customerID,itemID,Integer.parseInt(text[1]),"+");  //update customer file
			}
			
	
		}
		return content;
	}

	@Override
	public String _return(String customerID, String itemID, short numberOfItem) {
		// TODO Auto-generated method stub
		//update customer file
				if(updateCstFile(customerID,itemID,numberOfItem,"-")==0)
					return "customer "+customerID+ " didn't have item "+itemID+", unable to return.";
				else
				{
					//update share table
					int num;
					if(inventory.containsKey(itemID))
					{
						int index=(inventory.get(itemID)).getIndex();
						try {
							
							semaphore[index].acquire(); //use semaphore to lock item
							
							num=(inventory.get(itemID)).getNumberOfItem();
							num=num+numberOfItem;
							inventory.remove(itemID);
							inventory.put(itemID, new Item(itemID,num,index));
							
							semaphore[index].release(); //release item
							
						}catch(InterruptedException ex) {  
							ex.printStackTrace();  
							return "error: "+ex.getMessage();
						} 
					}
					else
					{
						int index=semaphore.length;
						inventory.put(itemID, new Item(itemID,numberOfItem,index));
						semaphore[index]=new Semaphore(1);
					}
					saveMapToFile(fileName);
					return "successfully returning "+numberOfItem+ " of item "+itemID+" from customer "+customerID+" to store " +fileName;
				}
	}

	@Override
	public String checkStock(String itemID, StringHolder storeName,
			ShortHolder numberOfItem) {
		// TODO Auto-generated method stub
		String content="";
		int num=-1;
		if(inventory.containsKey(itemID))
		{
			num=(inventory.get(itemID)).getNumberOfItem();
			//store return information into storename,numberOfItem
			storeName.value=fileName;
			numberOfItem.value=(short) num;
			content=content+"store "+fileName +" has item "+itemID+" :"+num+"\r\n";
		}
		//go to other two stores to check 
		/*specify udp port number
		 * Montreal server:8888
		 * Toronto server:9999
		 * Vancouver server:6790
		 * 
		 * Montreal client:8887
		 * Toronto client:9998
		 * Vancouver client:6789
		 */
		UDPClient client1;
		UDPClient client2;

		String rtnmessage="";
		String sendmessage;
		String []text;
		int switch_val = 0;
		if (fileName.equals("Montreal")) {
			switch_val = 1;
		} else if (fileName.equals("Toronto")) {
			switch_val = 2;
		} else if (fileName.equals("Vancouver")) {
			switch_val = 3;
		}
		switch(switch_val)
		{
			case 1:
				client1=new UDPClient();
				client2=new UDPClient();
				
			try {
				sendmessage="checkStock,"+itemID;
				rtnmessage=client1.sendMessage(sendmessage,"8887","9999");
				text=rtnmessage.split(",");
				if(text[0].equals("checkStock"))
					rtnmessage=text[1];
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("UDP connect to server error:"+e.getMessage());
			}
				//store return information into storeName,numberOfItem
				storeName.value="Toronto";
				numberOfItem.value=(short) Integer.parseInt(rtnmessage);
				content=content+"store Toronto has item "+itemID+" :"+rtnmessage+"\r\n";
				
			try {
				sendmessage="checkStock,"+itemID;
				rtnmessage=client2.sendMessage(sendmessage,"8887","6790");
				text=rtnmessage.split(",");
				if(text[0].equals("checkStock"))
					rtnmessage=text[1];
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("UDP connect to server error:"+e.getMessage());
			}
				//store return information into storeName,numberOfItem
				storeName.value="Vancouver";
				numberOfItem.value=(short) Integer.parseInt(rtnmessage);
				content=content+"store Vancouver has item "+itemID+" :"+rtnmessage+"\r\n";
				break;
			case 2:
				client1=new UDPClient();
				client2=new UDPClient();
				
			try {
				sendmessage="checkStock,"+itemID;
				rtnmessage=client1.sendMessage(sendmessage,"9998",port_8888);
				text=rtnmessage.split(",");
				if(text[0].equals("checkStock"))
					rtnmessage=text[1];
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("UDP connect to server error:"+e.getMessage());
			}
				//store return information into storeName,numberOfItem
				storeName.value="Montreal";
				numberOfItem.value=(short) Integer.parseInt(rtnmessage);
				content=content+"store Montreal has item "+itemID+" :"+rtnmessage+"\r\n";
				
			try {
				sendmessage="checkStock,"+itemID;
				rtnmessage=client2.sendMessage(sendmessage,"9998","6790");
				text=rtnmessage.split(",");
				if(text[0].equals("checkStock"))
					rtnmessage=text[1];
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("UDP connect to server error:"+e.getMessage());
			}
				//store return information into storeName,numberOfItem
				storeName.value="Vancouver";
				numberOfItem.value=(short) Integer.parseInt(rtnmessage);
				content=content+"store Vancouver has item "+itemID+" :"+rtnmessage+"\r\n";
				break;
			case 3:
				client1=new UDPClient();
				client2=new UDPClient();
		
			try {
				sendmessage="checkStock,"+itemID;
				rtnmessage=client1.sendMessage(sendmessage,"6789",port_8888);
				text=rtnmessage.split(",");
				if(text[0].equals("checkStock"))
					rtnmessage=text[1];
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("UDP connect to server error:"+e.getMessage());
			}
				//store return information into storeName,numberOfItem
				storeName.value="Montreal";
				numberOfItem.value=(short) Integer.parseInt(rtnmessage);
				content=content+"store Montreal has item "+itemID+" :"+rtnmessage+"\r\n";
				
			try {
				sendmessage="checkStock,"+itemID;
				rtnmessage=client2.sendMessage(sendmessage,"6789","9999");
				text=rtnmessage.split(",");
				if(text[0].equals("checkStock"))
					rtnmessage=text[1];
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("UDP connect to server error:"+e.getMessage());
			}
				//store return information into storeName,numberOfItem
				storeName.value="Vancouver";
				numberOfItem.value=(short) Integer.parseInt(rtnmessage);
				content=content+"store Vancouver has item "+itemID+" :"+rtnmessage+"\r\n";
				break;
		}	
		return content;
	}

	@Override
	public String exchange(String customerID, String boughtItemID,
			short boughtNumber, String desiredItemID, short desiredNumber) {
		// TODO Auto-generated method stub
		//first check customerID bought at least boughtNumber of boughtItemID
				if(boughtNumber<=checkCstFile(customerID,boughtItemID))
				{
					//then check at least desiredNumber of desiredItemID is available
					if(inventory.containsKey(desiredItemID))
					{
						int num=(inventory.get(desiredItemID)).getNumberOfItem();
						if(desiredNumber<=num)
						{
							//return
							String returnInfor = _return(customerID,boughtItemID,boughtNumber);
							//buy
							String buyInfor = buy(customerID,desiredItemID,desiredNumber);
							
							return "The exchange information result is--- "+returnInfor+" and "+buyInfor;
							
							
						}
					}
				}
				return null;
	}
	
	private void initializeMap(String filename)
	{
		
		//when read, we imply a  read lock
		readStore.lock();
		//read items from file to initialize the hashmap
		try {
			File file=new File(filename);
			FileReader fr =new FileReader(file.getAbsoluteFile());
			BufferedReader input = new BufferedReader(fr);
			String text;
			int lineNum=0;
			text=input.readLine();
			while (text !=null && !text.trim().equals("")) { 
				String[] items=text.split(",");
			    //process 
				//store items into hashtable
				//itemTable.put(items[0],Integer.parseInt(items[1]));
				// store itemID and Item object
				inventory.put(items[0],new Item(items[0],Integer.parseInt(items[1]),lineNum));
				text=input.readLine();
				lineNum++;
			}
		}
		catch(Exception e) {
			System.out.println("Error opening the file.");
			System.exit(0);
		}finally {
			readStore.unlock();
		}
	}
	
	private void saveMapToFile(String filename)
	{
		//when update store file from hashtable, need to lock the operation
		writeStore.lock();
		File file=new File(filename);
		try {
		FileWriter fw =new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		
		//iterate share table
		
		Enumeration<String> keys = inventory.keys();
		while( keys.hasMoreElements()) 
		{
			String key = (String) keys.nextElement();
			//int value = (int) itemTable.get(key);
			int value=(inventory.get(key)).getNumberOfItem();
			bw.write(key+","+value);
			bw.newLine();
		}
	
		bw.flush();
		bw.close();
		}
		catch(IOException e)
		{
			System.out.println("write "+filename+" failed.");
		}finally {
			writeStore.unlock();
		}
	}
	
	private int updateCstFile(String customerID, String itemID, int numberOfItem,String op)
	{
		//writeCustomer.lock();
		//write to customer file
		int rtnvalue=1;
		try {
			File file = new File(customerID);
			//if file doesn't exists, then create it
			if(!file.exists())
			{
				if(op.equals("+"))
				{
					System.out.println("Before buying customer "+customerID+" has nothing.");
					file.createNewFile();
					FileWriter fw =new FileWriter(file.getAbsoluteFile());
					BufferedWriter bw = new BufferedWriter(fw);
					bw.write(customerID+","+itemID+","+numberOfItem);
					bw.newLine();
					bw.flush();
					bw.close();
					return rtnvalue;
				}
				else
				{
					System.out.println("customer "+customerID+" has nothing to return.");
					rtnvalue=0;
					return rtnvalue;
				}
			}
			else
			{
				//read original file to content
				//return the items that customer had already bought
				System.out.println("Customer "+customerID+" had already bought: ");
				
				String content="";
				boolean update=false;   //indicate customer file exists
				BufferedReader fr = new BufferedReader(new FileReader(file));
				String text;
				String numitem;
				text=fr.readLine();
				while (text !=null && !text.trim().equals("")) { 
					String[] items=text.split(",");
				    //process 
					
					numitem=items[2].replaceAll("/r/n", "");
					System.out.println("items "+items[1]+" :"+items[2]);
					if(items[1].equals(itemID))
					{
						update=true;
						if(op.equals("+")) 
						{
							numitem=Integer.toString((Integer.parseInt(numitem)+numberOfItem));
							System.out.println("buy "+numberOfItem+" "+items[1]);
						}
						else
						{
							if(numberOfItem<=Integer.parseInt(numitem))
							{
								numitem=Integer.toString((Integer.parseInt(numitem)-numberOfItem));
								System.out.println("return "+numberOfItem+" "+items[1]);
							}
						}
					}
					content=content+items[0]+","+items[1]+","+numitem+"\r\n";
					text=fr.readLine();
				}//end while
				if(!update&&op.equals("+"))
					content=content+customerID+","+itemID+","+numberOfItem+"\r\n";
				if(!update&&op.equals("-"))
					rtnvalue=0;
				//rewrite file
				file.createNewFile();
				FileWriter fw =new FileWriter(file.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
				System.out.println("After operation, customer file is \r\n"+content);
				bw.write(content);
				bw.flush();
				bw.close();
				
			}//end if
		}catch(IOException e) {
			e.printStackTrace();
		}finally {
			//writeCustomer.unlock();
		}
		return rtnvalue;
	}
	
	//go to customerID file to check the number of itemID that customerID bought and return 
	private int checkCstFile(String customerID, String itemID)
	{
		int num=-1;
		try {
			File file = new File(customerID);
			//if file doesn't exists, then create it
			if(file.exists())
			{
				//read customer file 
				BufferedReader fr = new BufferedReader(new FileReader(file));
				String text;
				String numitem;
				text=fr.readLine();
				while (text !=null && !text.trim().equals("")) { 
					String[] items=text.split(",");
				    //process 
					
					numitem=items[2].replaceAll("/r/n", "");
					System.out.println("items "+items[1]+" :"+items[2]);
					if(items[1].equals(itemID))
					{
						num=Integer.parseInt(numitem);
						return num;
					}
					
					text=fr.readLine();
				}//end while
			}
		}catch(IOException e) {
			e.printStackTrace();
		}finally {
			//writeCustomer.unlock();
		}
		return num;
	}
	
	//implement shutdown method
	public void shutdown()
	{
		orb.shutdown(false);
	}
}

	

