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

package client;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.util.Random;

import common.DRSCommon;
import common.DRSServerCommon;
import common.SvrInfo;
import common.SysLogger;

public class ConcurrentTest implements Runnable {

	private String svrName = SvrInfo.SVR_NAME_MONTREAL;
	private String customerID = "M10001";
	private String itemID = DRSServerCommon.TEST_ITEMID;
	private int numberOfItem = 10;
	
	public ConcurrentTest(String svrName_, String customerID_, String itemID_, int numberOfItem_) {
		svrName = svrName_;
		customerID = customerID_;
		itemID = itemID_;
		numberOfItem = numberOfItem_;
	}

	private void simpleTestBuy(DRSCommon svr, String svrName, String customerID, String itemID, int numberOfItem) throws RemoteException {
		String ret = "";
		int buyRet = -1;
		
		buyRet = svr.buy(customerID, itemID, numberOfItem);
		if (buyRet == 0) {
			SysLogger.info("buy successfully. " + customerID + ", " + itemID + ", " + numberOfItem);
		} else {
			SysLogger.info("!! failed to buy. " + customerID + ", " + itemID + ", " + numberOfItem);
		}

		ret = svr.checkStock(itemID);
		SysLogger.info("checkStock " + itemID + ": " + ret);		
	}

	private void simpleTestReturn(DRSCommon svr, String svrName, String customerID, String itemID, int numberOfItem) throws RemoteException {
		String ret = "";
		int buyRet = -1;
		
		buyRet = svr.returnNumOfItem(customerID, itemID, numberOfItem);
		if (buyRet == 0) {
			SysLogger.info("return successfully. " + customerID + ", " + itemID + ", " + numberOfItem);
		} else {
			SysLogger.info("!! failed to return. " + customerID + ", " + itemID + ", " + numberOfItem);
		}

		ret = svr.checkStock(itemID);
		SysLogger.info("checkStock " + itemID + ": " + ret);		
	}
	
	public void simpleTest(String svrName, String customerID, String itemID, int numberOfItem) {
		try {
			System.setSecurityManager(new RMISecurityManager());
			DRSCommon svr = null;
			svr = (DRSCommon) Naming.lookup("rmi://localhost/" + svrName);
			
			simpleTestBuy(svr, svrName, customerID, itemID, numberOfItem);
			simpleTestBuy(svr, svrName, customerID, itemID, numberOfItem);
			simpleTestBuy(svr, svrName, customerID, itemID, numberOfItem);
			
			// now all the stocks are empty, the following will be failed. 
			simpleTestBuy(svr, svrName, customerID, itemID, numberOfItem);
			
			// now return some items
			simpleTestReturn(svr, svrName, customerID, itemID, numberOfItem);
			
			// buy again
			simpleTestBuy(svr, svrName, customerID, itemID, numberOfItem);
			
			// now all the stocks are empty, the following will be failed. 
			simpleTestBuy(svr, svrName, customerID, itemID, numberOfItem);
			
			SysLogger.info("-----------------Simple test END-----------------");

		} catch (Exception e) {
			StringWriter err = new StringWriter();
			e.printStackTrace(new PrintWriter(err));
			SysLogger.err(err.toString());
		}
		
	}
	
	@Override
	public void run() {
		try {
			SysLogger.info("Client test thread starts: svr=" + svrName + ", customerID=" + customerID);

			System.setSecurityManager(new RMISecurityManager());
			DRSCommon svr = null;
			svr = (DRSCommon) Naming.lookup("rmi://localhost/" + svrName);
			
			numberOfItem = 5;
			
			Random rd = new Random(System.nanoTime());			
			int i = 0, max = Math.abs(rd.nextInt() % 15) + 5;
			
			SysLogger.info("ConcurrentTest: random num1: " + max);
			while (i++ < max) {
				simpleTestBuy(svr, svrName, customerID, itemID, numberOfItem);
			}

			i = 0;
			max = Math.abs(rd.nextInt() % 15) + 5;
			SysLogger.info("ConcurrentTest: random num2: " + max);
			while (i++ < max) {
				simpleTestReturn(svr, svrName, customerID, itemID, numberOfItem);
			}

			i = 0;
			max = Math.abs(rd.nextInt() % 15) + 5;
			SysLogger.info("ConcurrentTest: random num3: " + max);
			while (i++ < max) {
				simpleTestBuy(svr, svrName, customerID, itemID, numberOfItem);
			}

		} catch (Exception e) {
			StringWriter err = new StringWriter();
			e.printStackTrace(new PrintWriter(err));
			SysLogger.err(err.toString());
		}
		
	}
	
	public static void concurrentTest(String svrName, String customerID, String itemID, int numberOfItem) {
		(new Thread(new ConcurrentTest(svrName, customerID, itemID, numberOfItem))).start();
	}


}
