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

public class Sequencer {
	public void multicast(String feid) {
		broadcast(Conf.RM_CMD_SEQUENCER + "," + feid);
	}
	
	public static int sendRequest(String req, String hostName, int hostPort) {
		DatagramSocket udpSock = null;
		try {
			udpSock = new DatagramSocket();
			byte[] buf = req.getBytes();

			InetAddress host = InetAddress.getByName(hostName);
			DatagramPacket request = new DatagramPacket(buf, req.length(),
					host, hostPort);

			udpSock.send(request);
			SysLogger.info("Sequencer UDP SEND: " + new String(request.getData())
					+ " to " + request.getAddress().getHostAddress() + ":" + request.getPort());
			udpSock.close();

			return 0;

		} catch (Exception e) {
			StringWriter err = new StringWriter();
			e.printStackTrace(new PrintWriter(err));
			SysLogger.err(err.toString());
		}
		return 0;
	}

	public static void broadcast(String req) {
		sendRequest(req, Conf.RM_NAME_DRS1, Conf.RM_PORT_DRS1);
		sendRequest(req, Conf.RM_NAME_DRS2, Conf.RM_PORT_DRS2);
		sendRequest(req, Conf.RM_NAME_DRS3, Conf.RM_PORT_DRS3);
	}
}
