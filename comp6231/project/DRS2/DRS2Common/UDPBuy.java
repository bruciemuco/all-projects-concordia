package DRS2Common;
 /* 
 * COMP6231-Assignment2-CORBA
 * Author: YICHEN LI
 * Student ID: 6389635
 */ 
public class UDPBuy {
	public UDPBuy()
	{
		
	}
	
	public String getBuyInfo(String fileName,String itemID,int num)
	{
		//using UDP to connect one closest store
		/*specify udp port number
		 * Montreal server:8888
		 * Toronto server:9999
		 * Vancouver server:6790
		 * 
		 * Montreal client:8887
		 * Toronto client:9998
		 * Vancouver client:6789
		 */
		String sendmessage;   //send message formate:operation,itemID,num
		String rtnmessage="";  //socket returned message
		String content="";    //return value
		String[] text;
		UDPClient client1=null;
		UDPClient client2=null;
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
				//first go to Toronto store 
				client1=new UDPClient();						
				try {
					sendmessage="buy,"+itemID+","+num;
					rtnmessage=client1.sendMessage(sendmessage,"8887","9999");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					System.out.println("UDP connect to server error:"+e.getMessage());
				}
				text=rtnmessage.split(",");
				if(text[0].equals("buy"))
				{
					if(text[1].equals("-1"))
					{
						content="Toronto,"+text[1];
						
						//second go to Vancouver store
					
						client2=new UDPClient();
						try {
							sendmessage="buy,"+itemID+","+(num);
							rtnmessage=client2.sendMessage(sendmessage,"8887","6790");
						} catch (Exception e) {
							// TODO Auto-generated catch block
							System.out.println("UDP connect to server error:"+e.getMessage());
						}
						text=rtnmessage.split(",");
						if(text[0].equals("buy"))
						{
							content="Vancouver,"+text[1];
						}
					
					}
					else
						content="Toronto,"+text[1];
				}		
			
				break;
			case 2:
				//first go to Montreal store
				client1=new UDPClient();
				
				try {
					sendmessage="buy,"+itemID+","+num;
					rtnmessage=client1.sendMessage(sendmessage,"9998","8888");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					System.out.println("UDP connect to server error:"+e.getMessage());
				}
				
				text=rtnmessage.split(",");
				if(text[0].equals("buy"))
				{
					content="Montreal,"+text[1];
					if(text[1].equals("-1"))
					{
						//second go to Vancouver store
					
						client2=new UDPClient();
						try {
							sendmessage="buy,"+itemID+","+(num);
							rtnmessage=client2.sendMessage(sendmessage,"9998","6790");
						} catch (Exception e) {
							// TODO Auto-generated catch block
							System.out.println("UDP connect to server error:"+e.getMessage());
						}
						text=rtnmessage.split(",");
						if(text[0].equals("buy"))
						{
							content="Vancouver,"+text[1];
						}
					
					}
					else
						content="Montreal,"+text[1];
				}
				
				break;
			case 3:
				//first go to Toronto store 
				client1=new UDPClient();						
				try {
					sendmessage="buy,"+itemID+","+num;
					rtnmessage=client1.sendMessage(sendmessage,"6789","9999");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					System.out.println("UDP connect to server error:"+e.getMessage());
				}
				text=rtnmessage.split(",");
				if(text[0].equals("buy"))
				{
					if(text[1].equals("-1"))
					{
						content="Toronto,"+text[1];
						//second go to Montreal store
					
						client2=new UDPClient();
						try {
							sendmessage="buy,"+itemID+","+(num);
							rtnmessage=client2.sendMessage(sendmessage,"6789","8888");
						} catch (Exception e) {
							// TODO Auto-generated catch block
							System.out.println("UDP connect to server error:"+e.getMessage());
						}
						text=rtnmessage.split(",");
						if(text[0].equals("buy"))
						{
							content="Montreal,"+text[1];
						}
					
					}
					else
						content="Toronto,"+text[1];
				}		
			
				break;
		}
		return content;
	}
}
