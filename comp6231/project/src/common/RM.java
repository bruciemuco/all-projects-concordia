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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;


class RMUDPLibs implements Runnable {
	public final int UDPBUFSIZE = 1500;
	public static boolean bExitThread = false;

	// a thread waiting for requests from FE
	@Override
	public void run() {
		DatagramSocket udpSock = null;
		try {
			udpSock = new DatagramSocket(RM.hostUDPPort);

			while (!bExitThread) {
				byte[] buf = new byte[UDPBUFSIZE];
				DatagramPacket request = new DatagramPacket(buf, buf.length);
				udpSock.receive(request);

				String req = new String(request.getData(), 0,
						request.getLength());
				String ip = request.getAddress().getHostAddress();
				int port = request.getPort();
				SysLogger.info("RM UDP RECV: " + req + ". from: " 
					+ ip + ":" + port);
								
				String[] reqList = req.split(",");
				if (reqList[0].equals("" + Conf.RM_CMD_SEQUENCER)) {
					// if it is a sequencer message
					RM.add2SequencerList(req);
					
				} else if (reqList[0].equals("" + Conf.FE_CMD_REPLACE)) {
					// replace the server
					RM.replaceHandler(req);
					
				} else if (reqList[0].equals("" + Conf.RM_CMD_SYNC_DATA_REQ)) {
					// get synchronous the data for remote servers
					RM.getSyncDataHandler(req);

				} else if (reqList[0].equals("" + Conf.RM_CMD_SYNC_DATA_RESP)) {
					// set synchronous the data for new replaced servers
					RM.setSyncDataHandler(req);

				} else {
					// store the request to the list
					RM.add2RequestList(req + "," + ip + "," + port);
				}			
				
				//notifyAll();
				RM.sem.release();
			}
			udpSock.close();
		} catch (Exception e) {
			StringWriter err = new StringWriter();
			e.printStackTrace(new PrintWriter(err));
			SysLogger.err(err.toString());
		}

	}

	public static Thread t = null;
	public void udpServerStart() {
		t = new Thread(new RMUDPLibs());
		t.start();
	}

	public static int sendRequest(String req, String hostIP, int hostPort) {
		//SysLogger.info("RM UDP before sending: " + req + ". " + hostIP + ":" + hostPort);

		DatagramSocket udpSock = null;
		try {
			udpSock = new DatagramSocket();
			byte[] buf = req.getBytes();

			InetAddress host = InetAddress.getByName(hostIP);
			DatagramPacket request = new DatagramPacket(buf, req.length(),
					host, hostPort);

			udpSock.send(request);
			SysLogger.info("RM UDP SEND: " + new String(request.getData())
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

}

public class RM {
	// to store the requests from FE
	private static LinkedList<String> reqs = new LinkedList<String>();
	
	// to store the ordering message from sequncer
	private static LinkedList<String> seqs = new LinkedList<String>();

	public static String RMName;
	public static int hostUDPPort;
	
	RMUDPLibs RMUDP = new RMUDPLibs();
	
	public static synchronized void add2RequestList(String e) {
		reqs.add(e);
	}
	public static synchronized void add2SequencerList(String e) {
		seqs.add(e);
	}
	
	private synchronized int getNumOfReqs() {
		return reqs.size();
	}
	private synchronized int getNumOfSeqs() {
		return seqs.size();
	}
	private synchronized String getSeq() {
		try {
			return seqs.getFirst();

		} catch (Exception e) {
			return null;
		}
	}
	private synchronized String getReq() {
		try {
			return reqs.getFirst();

		} catch (Exception e) {
			return null;
		}
	}
	private synchronized String getReq(String feid) {
		for (int i = 0; i < reqs.size(); i++) {
			if (feid.equals(reqs.get(i).split(",")[1])) {
				return reqs.get(i);
			}
		}
		return null;
	}
	private synchronized void delSeq() {
		seqs.remove();
	}
	private synchronized void delReq(String feid) {
		for (int i = 0; i < reqs.size(); i++) {
			if (feid.equals(reqs.get(i).split(",")[1])) {
				reqs.remove(i);
			}
		}
	}
	
	private static Sequencer sqcer;
	public static void sqcerMulticast(String req) {
		if (sqcer != null && req != null) {			
			sqcer.multicast(req.split(",")[1]);
		}
	}

	private static ServersCommon svrs = null;
	private static boolean ifReplacing = false;
	public static int replaceHandler(String req) {
		// restart all servers(M/T/V) as there are interactions 
		// between servers, and FE does not know which server is wrong
		//svrs.restartAllServers();
		
		// now synchronous the data
		SysLogger.info("start to synchronous data for all servers...");
		ifReplacing = true;
		
		// request format: RM_CMD_SYNC_DATA_REQ,name of this RM
		if (RMName.equals(Conf.RM_NAME_DRS1)) {
			RMUDPLibs.sendRequest(Conf.RM_CMD_SYNC_DATA_REQ + "," + RMName, 
					Conf.RM_NAME_DRS2, Conf.RM_PORT_DRS2);
			SysLogger.info("send SYNC request to :" + Conf.RM_NAME_DRS2);
			
		} else if (RMName.equals(Conf.RM_NAME_DRS2)) {
			RMUDPLibs.sendRequest(Conf.RM_CMD_SYNC_DATA_REQ + "," + RMName, 
					Conf.RM_NAME_DRS1, Conf.RM_PORT_DRS1);
			SysLogger.info("send SYNC request to :" + Conf.RM_NAME_DRS1);
			
		} else if (RMName.equals(Conf.RM_NAME_DRS3)) {
			RMUDPLibs.sendRequest(Conf.RM_CMD_SYNC_DATA_REQ + "," + RMName, 
					Conf.RM_NAME_DRS1, Conf.RM_PORT_DRS1);
			SysLogger.info("send SYNC request to :" + Conf.RM_NAME_DRS1);
		}
		
		return 0;
	}
	
	public RM(String name, int port, boolean isSequencer) {
		// initialize SysLogger
		SysLogger.init(name + ".txt");
		
		SysLogger.info("RM name: " + name);
		
		RMName = name;
		hostUDPPort = port;
		svrs = new ServersCommon(RMName);
		
		RMUDP.udpServerStart();
		
		if (isSequencer) {
			sqcer = new Sequencer();
		}
		
		svrs.startAllServers();
		
		// wait until udp server has started
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			StringWriter err = new StringWriter();
			e.printStackTrace(new PrintWriter(err));
			SysLogger.err(err.toString());
		}		
	}
	
	// work in a blocking way
	public static Semaphore sem = new Semaphore(0);
	public void seqMsgHandler() {
		while (true) {
			try {
				//wait();
				sem.acquire();
			} catch (Exception e) {
				StringWriter err = new StringWriter();
				e.printStackTrace(new PrintWriter(err));
				SysLogger.err(err.toString());
			}
			
			if (ifReplacing) {
				continue;
			}

			if (getNumOfSeqs() > 0) {
				if (getNumOfReqs() > 0) {
					// deal with the request in terms of the order
					reqHandler();
					
				}
				// now the FE request has not been arrived yet, continue waiting
				
			} else {
				// notify the sequencer to send ordering message
				RM.sqcerMulticast(getReq());
			}
		}
		
	}
	
	private int reqHandler() {
		// get the first element from reqs list
		String seq = getSeq();
		String feid = seq.split(",")[1];
		
		// traverse the list of reqs, get the one of same feid.
		String req = getReq(feid);
		if (req == null) {
			SysLogger.info("reqHandler: no req for FEID: " + feid);
			return -1;
		}
		
		// corba invocation
		corbaCall(req);
		
		// delete the ordering message and req
		delSeq();
		delReq(feid);
		
		return 0;
	}
	
	private int corbaCall(String req) {
		String[] tmp = req.split(",");
		
		int cmd = Integer.parseInt(tmp[0]);
		String ret = "";
		String host = "";
		int port = 0;		
		ClientCommon drsClient = null;
		
		SysLogger.info("start corba invocation: RM: " + RMName + ", Request: " + req);
		switch (cmd) {
		case Conf.FE_CMD_BUY:
			drsClient = new ClientCommon(RMName, tmp[2]);
			ret = "" + drsClient.buy(tmp[2], tmp[3], Integer.parseInt(tmp[4]));
			host = tmp[5];
			port = Integer.parseInt(tmp[6]);
			break;
		case Conf.FE_CMD_RETURN:
			drsClient = new ClientCommon(RMName, tmp[2]);
			ret = "" + drsClient.returnNumOfItem(tmp[2], tmp[3], Integer.parseInt(tmp[4]));
			host = tmp[5];
			port = Integer.parseInt(tmp[6]);
			break;
		case Conf.FE_CMD_CHECK:
			drsClient = new ClientCommon(RMName, null);
			ret = drsClient.checkStock(tmp[2]);
			host = tmp[3];
			port = Integer.parseInt(tmp[4]);
			break;
		case Conf.FE_CMD_EXCHANGE:
			drsClient = new ClientCommon(RMName, tmp[2]);
			ret = "" + drsClient.exchange(tmp[2], tmp[3], Integer.parseInt(tmp[4]), tmp[5], Integer.parseInt(tmp[6]));
			host = tmp[7];
			port = Integer.parseInt(tmp[8]);
			break;

		default:
			return -1;
		}
		
		// send reply to FE
		RMUDPLibs.sendRequest(RMName + "," + ret, host, port);
		return 0;
	}
	
	public static int setSyncDataHandler(String data) {
		int ret = svrs.setSyncData(data);
		ifReplacing = false;
		return ret;
	}

	public static int getSyncDataHandler(String req) {
		String data = svrs.getSyncData();
		String RMName = req.split(",")[1];
	
		if (RMName.equals(Conf.RM_NAME_DRS1)) {
			RMUDPLibs.sendRequest(Conf.RM_CMD_SYNC_DATA_RESP + "," + data, 
					RMName, Conf.RM_PORT_DRS1);

		} else if (RMName.equals(Conf.RM_NAME_DRS2)) {
			RMUDPLibs.sendRequest(Conf.RM_CMD_SYNC_DATA_RESP + "," + data, 
					RMName, Conf.RM_PORT_DRS2);

		} else if (RMName.equals(Conf.RM_NAME_DRS3)) {
			RMUDPLibs.sendRequest(Conf.RM_CMD_SYNC_DATA_RESP + "," + data, 
					RMName, Conf.RM_PORT_DRS3);
		}

		return 0;
	}
	
	public void start() {
		try {
			seqMsgHandler();
			RMUDP.t.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
