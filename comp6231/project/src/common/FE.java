/*
 * COMP6231 Project
 * 
 * This file is created by Yuan Tao (ewan.msn@gmail.com)
 * Licensed under GNU GPL v3
 * 
 * $Author: ewan.msn@gmail.com $
 * $Date: 2012-10-28 00:12:47 -0400 (Sun, 28 Oct 2012) $
 * $Rev: 181 $
 * $HeadURL: https://all-projects-concordia.googlecode.com/svn/comp6231/assignment2/src/common/SvrInfo.java $
 * 
 */

package common;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Semaphore;

class FEUDPLibs implements Runnable {
	public static final int UDPBUFSIZE = 1500;
	public static boolean bExitThread = false;
	private int udpListenPort;
	private DatagramSocket udpSock = null;
	
	public void createSock(int port) {
		try {			
			udpSock = new DatagramSocket(port);
			udpListenPort = udpSock.getLocalPort();
			
		} catch (SocketException e) {
			StringWriter err = new StringWriter();
			e.printStackTrace(new PrintWriter(err));
			SysLogger.err(err.toString());
		}
	}
	
	public FEUDPLibs(DatagramSocket sock) {
		udpSock = sock;
	}
	
	// a thread waiting for the RM's responses
	@Override
	public void run() {
		udpListenPort = udpSock.getLocalPort();
		
		try {
			//udpSock = new DatagramSocket(udpListenPort);

			while (!bExitThread) {
				byte[] buf = new byte[UDPBUFSIZE];
				DatagramPacket request = new DatagramPacket(buf, buf.length);
				udpSock.receive(request);

				String resp = new String(request.getData(), 0,
						request.getLength());
				SysLogger.info("FE UDP(port:" + udpListenPort + ") RECV: " + resp);

				FE.resp.add(resp);
				if (FE.resp.size() == 3) {
					//notifyAll();
					FE.sem.release();
				}
			}
			udpSock.close();
		} catch (Exception e) {
			StringWriter err = new StringWriter();
			e.printStackTrace(new PrintWriter(err));
			SysLogger.err(err.toString());
		}

	}

	public void udpServerStart() {
		//udpListenPort = port;
		(new Thread(new FEUDPLibs(udpSock))).start();
	}

	public int sendRequest(String req, String hostName, int hostPort) {
		//DatagramSocket udpSock = null;
		try {
			//udpSock = new DatagramSocket();
			byte[] buf = req.getBytes();

			InetAddress host = InetAddress.getByName(hostName);
			DatagramPacket request = new DatagramPacket(buf, req.length(),
					host, hostPort);

			udpSock.send(request);
			SysLogger.info("FE UDP SEND: " + new String(request.getData())
					+ " to " + request.getAddress().getHostAddress() + ":" + request.getPort());
			//udpSock.close();

			return 0;

		} catch (Exception e) {
			StringWriter err = new StringWriter();
			e.printStackTrace(new PrintWriter(err));
			SysLogger.err(err.toString());
		}
		return 0;
	}

	public void broadcast(String req) {
		sendRequest(req, Conf.RM_NAME_DRS1, Conf.RM_PORT_DRS1);
		sendRequest(req, Conf.RM_NAME_DRS2, Conf.RM_PORT_DRS2);
		sendRequest(req, Conf.RM_NAME_DRS3, Conf.RM_PORT_DRS3);
	}
}

public class FE implements DRSServices {
	public static ArrayList<String> resp = new ArrayList<String>();
	FEUDPLibs FEUDP = new FEUDPLibs(null);

	private int FEID;

	private int[] errCnt = new int[3];
	
	// test data
	public boolean testFlag = false;
	public int RMNo = 0;

	public FE() {
		clearErrs();
		
		Random rd = new Random(System.nanoTime());
		FEID = Math.abs(rd.nextInt());
		int port = Conf.FE_UDP_PORT_BASE + Math.abs(rd.nextInt()) % 300;
		
		FEUDP.createSock(port);
		FEUDP.udpServerStart();
		SysLogger.info("FE: " + FEID + ", UDP listening port: " + port);

		// wait until udp server has started
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			StringWriter err = new StringWriter();
			e.printStackTrace(new PrintWriter(err));
			SysLogger.err(err.toString());
		}
	}

	private void clearErrs() {
		errCnt[0] = 0;
		errCnt[1] = 0;
		errCnt[2] = 0;
	}
	
	private void addErrCnt(String RMName) {
		SysLogger.info("get an error from: " + RMName);
		if (RMName.equals(Conf.RM_NAME_DRS1)) {
			errCnt[0]++;
			if (errCnt[0] == 3) {
				// notify the corresponding RM to replace the server
				FEUDP.sendRequest(Conf.FE_CMD_REPLACE + "," + RMName,
						RMName, Conf.RM_PORT_DRS1);
				errCnt[0] = 0;
				return;
			}
		}
		if (RMName.equals(Conf.RM_NAME_DRS2)) {
			errCnt[1]++;
			if (errCnt[1] == 3) {
				// notify the corresponding RM to replace the server
				FEUDP.sendRequest(Conf.FE_CMD_REPLACE + "," + RMName,
						RMName, Conf.RM_PORT_DRS2);
				errCnt[1] = 0;
				return;
			}
		}
		if (RMName.equals(Conf.RM_NAME_DRS3)) {
			errCnt[2]++;
			if (errCnt[2] == 3) {
				// notify the corresponding RM to replace the server
				FEUDP.sendRequest(Conf.FE_CMD_REPLACE + "," + RMName,
						RMName, Conf.RM_PORT_DRS3);
				errCnt[2] = 0;
				return;
			}
		}
	}

	public static Semaphore sem = new Semaphore(0);
	private String respHandler() {
		while (resp.size() < 3) {
			try {
				//wait();
				sem.acquire();
			} catch (Exception e) {
				StringWriter err = new StringWriter();
				e.printStackTrace(new PrintWriter(err));
				SysLogger.err(err.toString());
			}
		}

		// response format: RMName,Result
		String[] result1 = resp.get(0).split(",");
		String[] result2 = resp.get(1).split(",");
		String[] result3 = resp.get(2).split(",");
		
		if (testFlag) {
			switch (RMNo) {
			case 0:
				if (result1[0].equals(Conf.RM_NAME_DRS1)) {
					result1[1] = "TEST_ERROR";
				} else if (result2[0].equals(Conf.RM_NAME_DRS1)) {
					result2[1] = "TEST_ERROR";
				} else if (result3[0].equals(Conf.RM_NAME_DRS1)) {
					result3[1] = "TEST_ERROR";
				}
				
				break;
			case 1:
				if (result1[0].equals(Conf.RM_NAME_DRS2)) {
					result1[1] = "TEST_ERROR";
				} else if (result2[0].equals(Conf.RM_NAME_DRS2)) {
					result2[1] = "TEST_ERROR";
				} else if (result3[0].equals(Conf.RM_NAME_DRS2)) {
					result3[1] = "TEST_ERROR";
				}
				break;
			default:
				if (result1[0].equals(Conf.RM_NAME_DRS3)) {
					result1[1] = "TEST_ERROR";
				} else if (result2[0].equals(Conf.RM_NAME_DRS3)) {
					result2[1] = "TEST_ERROR";
				} else if (result3[0].equals(Conf.RM_NAME_DRS3)) {
					result3[1] = "TEST_ERROR";
				}
				break;
			}
		}
		
		if (result1[1].equals(result2[1])) {
			if (!result1[1].equals(result3[1])) {
				addErrCnt(result3[0]);
			}
			clearErrs();
			return result1[1];
		}

		if (result1[1].equals(result3[1])) {
			addErrCnt(result2[0]);
			return result1[1];
		}

		if (result2[1].equals(result3[1])) {
			addErrCnt(result1[0]);
			return result2[1];
		}

		// error
		SysLogger.err("Got two wrong response from three RMs");
		return null;
	}

	private String sendRequest(String req) {
		FEUDP.broadcast(req);
		String ret = respHandler();
		resp.clear();
		return ret;
	}

	@Override
	public int buy(String customerID, String itemID, int numberOfItem) {
		String ret = sendRequest(Conf.FE_CMD_BUY + "," + FEID + ","
				+ customerID + "," + itemID + "," + numberOfItem);
		if (ret != null) {
			return Integer.parseInt(ret);
		}
		return 0;
	}

	@Override
	public int returnNumOfItem(String customerID, String itemID,
			int numberOfItem) {
		String ret = sendRequest(Conf.FE_CMD_RETURN + "," + FEID + ","
				+ customerID + "," + itemID + "," + numberOfItem);
		if (ret != null) {
			return Integer.parseInt(ret);
		}
		return 0;
	}

	@Override
	public String checkStock(String itemID) {
		String ret = sendRequest(Conf.FE_CMD_CHECK + "," + FEID + "," + itemID);

		return ret;
	}

	@Override
	public int exchange(String customerID, String boughtItemID,
			int boughtNumber, String desiredItemID, int desiredNumber) {
		String ret = sendRequest(Conf.FE_CMD_EXCHANGE + "," + FEID + ","
				+ customerID + "," + boughtItemID + "," + boughtNumber + ","
				+ desiredItemID + "," + desiredNumber);
		if (ret != null) {
			return Integer.parseInt(ret);
		}
		return 0;
	}

}
