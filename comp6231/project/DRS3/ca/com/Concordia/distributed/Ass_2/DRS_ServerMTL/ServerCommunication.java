package ca.com.Concordia.distributed.Ass_2.DRS_ServerMTL;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ServerCommunication extends Thread
{
	DatagramSocket serverSocket;
	DatagramPacket receivePacket;
	byte[] receiveData=new byte[1024];
	byte[] sendData=null;
	MyServiceServerImpl impl;
	public ServerCommunication(MyServiceServerImpl impl)
	{
		try
		{
		    this.impl = impl;
		    serverSocket = new DatagramSocket(6789);
		    this.start();
		}
		catch(Exception e)
		{
			
		}
		
	}
	public void run()
	{
		System.out.println("ServerUDP is running");
		try
		{
			while(true)
			{
				receivePacket=new DatagramPacket(receiveData,receiveData.length);
				serverSocket.receive(receivePacket);
				makePurchase(receivePacket);
				send();
			}
		}
		catch(Exception e)
		{
			
		}
	}
	
	public void makePurchase(DatagramPacket receivePacket)
	{
        String receive = new String (receivePacket.getData(), 0, receivePacket.getLength());
		
		String itemID = receive.substring(0, 6);
		int customerID = Integer.parseInt(receive.substring(6,7));

		int numOfItem = Integer.parseInt(receive.substring(7,8));
		String purchaseInfo = impl.purchase(customerID, itemID, numOfItem);
		
		sendData = purchaseInfo.getBytes();
	}
	
	public void send()
	{
		try
		{
			DatagramPacket sendPacket=new DatagramPacket(sendData,sendData.length,receivePacket.getAddress(),receivePacket.getPort());
			serverSocket.send(sendPacket);
		}
		catch(Exception e)
		{
			
		}
	}
}

