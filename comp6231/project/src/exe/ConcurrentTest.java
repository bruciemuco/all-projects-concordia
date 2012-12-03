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

package exe;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Random;

import DRSCommon.DRSClientCommon;
import DRSCommon.DRSCommonService;
import DRSCommon.DRSServerCommon;

import common.FE;
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

	private void simpleTestBuy(FE svr, String svrName, String customerID, String itemID, int numberOfItem){
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

	private void simpleTestReturn(FE svr, String svrName, String customerID, String itemID, int numberOfItem){
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
	
	private void simpleTestExchange(FE svr, String svrName, String customerID, 
			String boughtItemID, int boughtNumber, String desiredItemID, int desiredNumber){
		String ret = "";
		int buyRet = -1;

		buyRet = svr.exchange(customerID, boughtItemID, boughtNumber, desiredItemID, desiredNumber);
		if (buyRet == 1) {
			SysLogger.info("exchange successfully. " + customerID + ", " + boughtItemID + ", " 
					+ numberOfItem + ", " + desiredItemID + ", " + desiredNumber);
		} else {
			SysLogger.info("!! failed to exchange. " + customerID + ", " + boughtItemID + ", " 
					+ numberOfItem + ", " + desiredItemID + ", " + desiredNumber);
		}

		ret = svr.checkStock(itemID);
		SysLogger.info("checkStock " + boughtItemID + ": " + ret);		
		ret = svr.checkStock(desiredItemID);
		SysLogger.info("checkStock " + desiredItemID + ": " + ret);		
	}
	
	public void simpleTest(String svrName, String customerID, String itemID, int numberOfItem) {
		try {
			FE svr = new FE();
			
			String ret = svr.checkStock(itemID);
			SysLogger.info("checkStock " + itemID + ": " + ret);		

			String desiredItemID = (Integer.parseInt(itemID) + 1) + "";
			ret = svr.checkStock(desiredItemID);
			SysLogger.info("checkStock " + desiredItemID + ": " + ret);		

			simpleTestBuy(svr, svrName, customerID, itemID, numberOfItem);
			simpleTestBuy(svr, svrName, customerID, itemID, numberOfItem);
			simpleTestBuy(svr, svrName, customerID, itemID, numberOfItem);
			simpleTestBuy(svr, svrName, customerID, itemID, numberOfItem);
			
			svr.testFlag = true;
			Random rd = new Random(System.nanoTime());
			svr.RMNo = Math.abs(rd.nextInt()) % 3;
			
			// now all the stocks are empty, the following buy will be failed. 
			simpleTestBuy(svr, svrName, customerID, itemID, numberOfItem);
			
			// now exchange items
			simpleTestExchange(svr, svrName, customerID, itemID, numberOfItem, desiredItemID, numberOfItem);
			
			// exchange again.
			simpleTestExchange(svr, svrName, customerID, itemID, numberOfItem, desiredItemID, numberOfItem);

			svr.testFlag = false;

			// exchange again. now it will be failed.
			simpleTestExchange(svr, svrName, customerID, itemID, numberOfItem, desiredItemID, numberOfItem);

			// buy twice
			simpleTestBuy(svr, svrName, customerID, itemID, numberOfItem);
			simpleTestBuy(svr, svrName, customerID, itemID, numberOfItem);

			// exchange again.
			simpleTestExchange(svr, svrName, customerID, itemID, numberOfItem, desiredItemID, numberOfItem);

			// exchange again. now it will be failed.
			simpleTestExchange(svr, svrName, customerID, itemID, numberOfItem, desiredItemID, numberOfItem);

			//svrObject.shutdown();
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
			//SysLogger.info("Client test thread starts: svr=" + svrName + ", customerID=" + customerID);

			FE svr = new FE();
			
			numberOfItem = 5;
			
			Random rd = new Random(System.nanoTime());			
			int i = 0, max = Math.abs(rd.nextInt() % 10) + 1;
			
			SysLogger.info("ConcurrentTest: random num1: " + max);
			while (i++ < max) {
				simpleTestBuy(svr, svrName, customerID, itemID, numberOfItem);
			}

			i = 0;
			max = Math.abs(rd.nextInt() % 15) + 5;
			SysLogger.info("ConcurrentTest: random num2: " + max);
			while (i++ < max) {
				//simpleTestReturn(svr, svrName, customerID, itemID, numberOfItem);
				String desiredItemID = DRSServerCommon.TEST_ITEMID_EXCHG;
				simpleTestExchange(svr, svrName, customerID, itemID, numberOfItem, desiredItemID, numberOfItem);
			}

			i = 0;
			max = Math.abs(rd.nextInt() % 15) + 5;
			SysLogger.info("ConcurrentTest: random num3: " + max);
			while (i++ < max) {
				simpleTestBuy(svr, svrName, customerID, itemID, numberOfItem);
			}
			
			//svrObject.shutdown();

		} catch (Exception e) {
//			StringWriter err = new StringWriter();
//			e.printStackTrace(new PrintWriter(err));
//			SysLogger.err(err.toString());
		}
		
		SysLogger.info("------ Thread done.");
		
	}
	
	public static Thread t1 = null;
	public static Thread t2 = null;;
	public static Thread t3 = null;;
	public static void concurrentTest(String svrName, String customerID, String itemID, int numberOfItem) {
		if (t1 == null) {
			t1 = new Thread(new ConcurrentTest(svrName, customerID, itemID, numberOfItem));
			t1.start();
		} else if (t2 == null) {
			t2 = new Thread(new ConcurrentTest(svrName, customerID, itemID, numberOfItem));
			t2.start();
		} else if (t3 == null) {
			t3 = new Thread(new ConcurrentTest(svrName, customerID, itemID, numberOfItem));
			t3.start();
		}
	}


}
