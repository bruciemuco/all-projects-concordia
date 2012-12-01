package DRS2Common;
 /* 
 * COMP6231-Assignment2-CORBA
 * Author: YICHEN LI
 * Student ID: 6389635
 */ 
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class UDPServer extends Thread  {
	private String UDPServerName;
	private String serverPort;
	
	//read write lock for read and write store file
	private final ReadWriteLock readwritelockStore=new ReentrantReadWriteLock();
	private final Lock readStore=readwritelockStore.readLock();
	private final Lock writeStore=readwritelockStore.writeLock();
		
	public UDPServer(String name,String serverPort) {
		this.UDPServerName=name;
		this.serverPort=serverPort;
	}
	
	public void run()
	{
		String rtnMessage="";
		DatagramSocket serverSocket =null;
		try{
			//serverSocket=new DatagramSocket(8888);  //server port
			serverSocket=new DatagramSocket(Integer.parseInt(serverPort));  //server port
			byte[] receiveData = new byte[1024];
	        byte[] sendData = new byte[1024];
	        while(true)
	        {
	        	DatagramPacket receivePacket = new DatagramPacket(receiveData,receiveData.length);
	        	serverSocket.receive(receivePacket);
	        	
	        	//received data
	        	//String sentence=new String(receivePacket.getData());
	        	String sentence=new String(receiveData);
	        	System.out.println("received:"+sentence);
	       
	        	//process:sentence records message is "operation,itemID,numberOfItem"
	        	String[] messages=sentence.trim().split(",");
	        	
	        	
	        	if(messages[0].equals("checkStock"))
	        	{
	        		rtnMessage=getNumOfItem(UDPServerName,messages[1]);
	        		rtnMessage="checkStock,"+rtnMessage;
	        	}
	        	else if(messages[0].equals("buy"))
	        	{
	        		rtnMessage=buyNumOfItem(UDPServerName,messages[1],Integer.parseInt(messages[2]));
	        		rtnMessage="buy,"+rtnMessage;
	        			
	        	}
	        	//send reply     	
	        	//sendData=rtnMessage.getBytes();
	        	sendData=rtnMessage.trim().getBytes();
	        	DatagramPacket sendPacket=new DatagramPacket(sendData,sendData.length,receivePacket.getAddress(),receivePacket.getPort());
	        	serverSocket.send(sendPacket);
	        }
		}catch (SocketException e)
		{
			System.out.println("Socket: " + e.getMessage());
		}catch (IOException e)
		{
			System.out.println("IO: " + e.getMessage());
		}finally {
			if(serverSocket!=null)
			{
				serverSocket.close();
				serverSocket=null;
			}
		}
}
	
	private String getNumOfItem(String hostName,String itemID)
	{
		String content="";
		readStore.lock();
		try {
			File file=new File(hostName);
			FileReader fr =new FileReader(file.getAbsoluteFile());
			BufferedReader input = new BufferedReader(fr);
			String text;
			
			text=input.readLine();
			while (text !=null && !text.trim().equals("")) { 
				String[] items=text.split(",");
			    //process 
				//check itemid
				if(items[0].equals(itemID.trim()))
				{
					content=items[1];
					break;
				}
				text=input.readLine();
			}
			input.close();
			fr.close();
		}
		catch(Exception e) {
			System.out.println("Error opening the file.");
			System.exit(0);
		}finally {
			readStore.unlock();
		}
		if(content.equals(""))
			return "0";
		else
			return content;
	}
	
	private String buyNumOfItem(String hostName,String itemID,int numOfItem)
	{
		String content="";
		writeStore.lock();
		try {
			File file=new File(hostName);
			FileReader fr =new FileReader(file.getAbsoluteFile());
			BufferedReader input = new BufferedReader(fr);
			String text;
			String itemContent="";
			text=input.readLine();
			while (text !=null && !text.trim().equals("")) { 
				String[] items=text.split(",");
			    //process 
				//check itemid
				if(items[0].equals(itemID.trim()))
				{
					if(numOfItem<=Integer.parseInt(items[1]))
					{
						items[1]=Integer.toString(Integer.parseInt(items[1])-numOfItem);
						content=Integer.toString(numOfItem);
					}
					else
					{
						content="-1";
					}
				}
				itemContent=itemContent+items[0]+","+items[1]+"\r\n";
				text=input.readLine();
			}
			
			if(content.equals(""))
				content="-1";
			//rewrite file
			if(!content.equals("-1"))
			{
				file.createNewFile();
				FileWriter fw =new FileWriter(file.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
				System.out.println("After operation, customer file is \r\n"+content);
				bw.write(itemContent);
				bw.flush();
				bw.close();
				input.close();
				fr.close();
			}
		}
		catch(Exception e) {
			System.out.println("Error opening the file.");
			System.exit(0);
		}finally {
			writeStore.unlock();
		}
		return content;
	}
}