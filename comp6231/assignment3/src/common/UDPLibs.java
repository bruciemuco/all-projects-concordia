/*
 * COMP6231 Project
 * 
 * This file is created by Yuan Tao (ewan.msn@gmail.com)
 * Licensed under GNU GPL v3
 * 
 * $Author$
 * $Date$
 * $Rev$
 * $HeadURL$
 * 
 */

package common;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPLibs implements Runnable {
	public static final int UDPBUFSIZE = 1500;
	public static int udpLocalSvrPort;
	public static boolean bExitThread = false;
	public static DRSServerCommon svr = null; 
	
	// a thread waiting for the client's requests
	@Override
	public void run() {
		DatagramSocket udpSock = null;
		try {
			udpSock = new DatagramSocket(udpLocalSvrPort);
			
			while (!bExitThread) {
				byte[] buf = new byte[UDPBUFSIZE];
				DatagramPacket request = new DatagramPacket(buf, buf.length);
				udpSock.receive(request);
				
				String itemID = new String(request.getData(), 0, request.getLength());
				SysLogger.info("UDPSVR RECV: checkStock request: " + itemID);
				if (svr == null) {
					SysLogger.err("UDPSVR svr == null");
					break;
				}
				int ret = svr.checkLocalStock(itemID);
				if (ret < 0) {
					ret = 0;
				}
				buf = ("" + ret).getBytes();
				DatagramPacket resp = new DatagramPacket(buf, buf.length, request.getAddress(), request.getPort());
				udpSock.send(resp);
				SysLogger.info("UDPSVR SEND: " + new String(resp.getData()) + " to " + request.getAddress() + ":" + request.getPort());
			}
			udpSock.close();
		} catch (Exception e) {
			StringWriter err = new StringWriter();
			e.printStackTrace(new PrintWriter(err));
			SysLogger.err(err.toString());
		}
		
	}
	
	public void udpServerStart() {
		(new Thread(new UDPLibs())).start();
	}

	public String udpCheckOtherStock(String itemID, int port) {
		DatagramSocket udpSock = null;
		try {
			udpSock = new DatagramSocket();
			byte[] buf = itemID.getBytes();
			InetAddress host = InetAddress.getByName("localhost");
			
			DatagramPacket request = new DatagramPacket(buf, itemID.length(), host, port);
			udpSock.send(request);
			SysLogger.info("UDP SEND: checkStock request: " + new String(request.getData()) + " to " + request.getAddress() + ":" + request.getPort());

			// wait for the result
			buf = new byte[UDPBUFSIZE];
			DatagramPacket reply = new DatagramPacket(buf, buf.length);
			udpSock.receive(reply);
			
			String ret = new String(reply.getData(), 0, reply.getLength());
			SysLogger.info("UDP RECV: " + ret);
			udpSock.close();
			
			return ret;
			
		} catch (Exception e) {
			StringWriter err = new StringWriter();
			e.printStackTrace(new PrintWriter(err));
			SysLogger.err(err.toString());
		}
		return "";
	}
}
