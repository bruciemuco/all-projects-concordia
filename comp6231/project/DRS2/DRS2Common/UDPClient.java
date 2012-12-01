package DRS2Common;
 /* 
 * COMP6231-Assignment2-CORBA
 * Author: YICHEN LI
 * Student ID: 6389635
 */ 
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;


public class UDPClient {

	public UDPClient()
	{
	}

	public String sendMessage(String message,String clientPort,String serverPort) throws Exception
	{
		String rtnMessage="";  //return message
		DatagramSocket clientSocket=null;
		try{
			
			//InetAddress clientHost=InetAddress.getLocalHost();
			int svrPort=Integer.parseInt(serverPort) ;
			InetSocketAddress clientHost = new InetSocketAddress("127.0.0.1", svrPort);  //specify server port
			//InetSocketAddress clientHost = new InetSocketAddress("127.0.0.1", 8888);  //specify server port
			byte[] sendData=new byte[1024];
			
			//place data in a byte array,send is itemID
			sendData=message.getBytes();
			
			//
			DatagramPacket sendPacket=new DatagramPacket(sendData,sendData.length,clientHost);
			
			//create a datagram socket,client local port
			
			//clientSocket=new DatagramSocket(9999);
			clientSocket=new DatagramSocket(Integer.parseInt(clientPort));
			//send 
			clientSocket.send(sendPacket);
			byte[] receiveData=new byte[1024];
			//receive packet
			DatagramPacket receivePacket = new DatagramPacket(receiveData,receiveData.length);
			//receive
			clientSocket.receive(receivePacket);
			
			//received data is numberOfItems
			rtnMessage=(new String(receivePacket.getData())).trim();
			System.out.println("From server: "+rtnMessage);
		}catch (SocketException e)
		{
			System.out.println("Socket: " + e.getMessage());
		}catch (IOException e)
		{
			System.out.println("IO: " + e.getMessage());
		}
		finally {
			if(clientSocket!=null)
			{
				clientSocket.close();
				clientSocket=null;
			}
		}
		
		return rtnMessage;
		
	}
}