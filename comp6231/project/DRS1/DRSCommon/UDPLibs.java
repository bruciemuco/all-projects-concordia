/*
 * COMP6231 Project
 * 
 * This file is created by Yuan Tao (ewan.msn@gmail.com)
 * Licensed under GNU GPL v3
 * 
 * $Author: ewan.msn@gmail.com $
 * $Date: 2012-10-28 00:12:47 -0400 (Sun, 28 Oct 2012) $
 * $Rev: 181 $
 * $HeadURL: https://all-projects-concordia.googlecode.com/svn/comp6231/assignment2/src/common/UDPLibs.java $
 * 
 */

package DRSCommon;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import common.SvrInfo;
import common.SysLogger;

public class UDPLibs implements Runnable {
	public final int UDPBUFSIZE = 1500;
	public int udpLocalSvrPort;
	public boolean bExitThread = false;
	public DRSServerCommon svr = null; 
	
	public UDPLibs(int port, DRSServerCommon s) {
		udpLocalSvrPort = port;
		svr = s;
	}
	
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
				if (udpLocalSvrPort - SvrInfo.SVR_PORT_MONTREAL < 5) {
					SysLogger.info("UDPSVR RECV: checkStock request: " + itemID);
				} else if (udpLocalSvrPort - SvrInfo.SVR2_PORT_MONTREAL < 5) {
					SysLogger.info("server udp receive: " + itemID);
				}
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
				if (udpLocalSvrPort - SvrInfo.SVR_PORT_MONTREAL < 5) {
					SysLogger.info("UDPSVR SEND: " + new String(resp.getData()) + " to " + request.getAddress() + ":" + request.getPort());
				} else if (udpLocalSvrPort - SvrInfo.SVR2_PORT_MONTREAL < 5) {
					//SysLogger.info("server udp send: " + new String(resp.getData()));
				}
			}
			udpSock.close();
		} catch (Exception e) {
			StringWriter err = new StringWriter();
			e.printStackTrace(new PrintWriter(err));
			SysLogger.err(err.toString());
		}
		
	}
	
	public void udpServerStart(int port) {
		(new Thread(new UDPLibs(port, svr))).start();
	}

	public String udpCheckOtherStock(String itemID, int port) {
		DatagramSocket udpSock = null;
		try {
			udpSock = new DatagramSocket();
			byte[] buf = itemID.getBytes();
			InetAddress host = InetAddress.getByName("localhost");
			
			DatagramPacket request = new DatagramPacket(buf, itemID.length(), host, port);
			udpSock.send(request);
			if (udpLocalSvrPort - SvrInfo.SVR_PORT_MONTREAL < 5) {
				SysLogger.info("UDP SEND: checkStock request: " + new String(request.getData()) + " to " + request.getAddress() + ":" + request.getPort());
			} else if (udpLocalSvrPort - SvrInfo.SVR2_PORT_MONTREAL < 5) {
				SysLogger.info("server udp send: " + new String(request.getData()));
			}

			// wait for the result
			buf = new byte[UDPBUFSIZE];
			DatagramPacket reply = new DatagramPacket(buf, buf.length);
			udpSock.receive(reply);
			
			String ret = new String(reply.getData(), 0, reply.getLength());
			if (udpLocalSvrPort - SvrInfo.SVR_PORT_MONTREAL < 5) {
				SysLogger.info("UDP RECV: " + ret);
			}
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
