/*
 * COMP6231 Project
 *  
 * This file is created by Yuan Tao (ewan.msn@gmail.com)
 * Licensed under GNU GPL v3
 * 
 * $Author: ewan.msn@gmail.com $
 * $Date: 2012-10-28 00:12:47 -0400 (Sun, 28 Oct 2012) $
 * $Rev: 181 $
 * $HeadURL: https://all-projects-concordia.googlecode.com/svn/comp6231/assignment3/src/client/ConcurrentTest.java $
 * 
 */

package drsclient;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Random;

import DRSCommon.*;

public class ConcurrentTest implements Runnable {
	private String svrName = SvrInfo.SVR_NAME_MONTREAL;
	private String customerID = "M10001";
	private String itemID = DRSCommonService.TEST_ITEMID;
	private int numberOfItem = 10;
	
	public ConcurrentTest(String svrName_, String customerID_, String itemID_, int numberOfItem_) {
		svrName = svrName_;
		customerID = customerID_;
		itemID = itemID_;
		numberOfItem = numberOfItem_;
	}

	private void simpleTestBuy(DRSCommonService svr, String svrName, String customerID, String itemID, int numberOfItem){
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

	private void simpleTestReturn(DRSCommonService svr, String svrName, String customerID, String itemID, int numberOfItem){
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
	
	private void simpleTestExchange(DRSCommonService svr, String svrName, String customerID, 
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
			DRSCommonService svr = new DRSCommonService(svrName);
			
			String ret = svr.checkStock(itemID);
			SysLogger.info("checkStock " + itemID + ": " + ret);		

			String desiredItemID = (Integer.parseInt(itemID) + 1) + "";
			ret = svr.checkStock(desiredItemID);
			SysLogger.info("checkStock " + desiredItemID + ": " + ret);		

			simpleTestBuy(svr, svrName, customerID, itemID, numberOfItem);
			simpleTestBuy(svr, svrName, customerID, itemID, numberOfItem);
			simpleTestBuy(svr, svrName, customerID, itemID, numberOfItem);
			simpleTestBuy(svr, svrName, customerID, itemID, numberOfItem);
			
			// now all the stocks are empty, the following buy will be failed. 
			simpleTestBuy(svr, svrName, customerID, itemID, numberOfItem);
			
			// now exchange items
			simpleTestExchange(svr, svrName, customerID, itemID, numberOfItem, desiredItemID, numberOfItem);
			
			// exchange again.
			simpleTestExchange(svr, svrName, customerID, itemID, numberOfItem, desiredItemID, numberOfItem);

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
			SysLogger.info("Client test thread starts: svr=" + svrName + ", customerID=" + customerID);

			DRSCommonService svr = new DRSCommonService(svrName);
			
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
				//simpleTestReturn(svr, svrName, customerID, itemID, numberOfItem);
				String desiredItemID = (Integer.parseInt(itemID) + 1) + "";
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
			StringWriter err = new StringWriter();
			e.printStackTrace(new PrintWriter(err));
			SysLogger.err(err.toString());
		}
		
	}
	
	public static void concurrentTest(String svrName, String customerID, String itemID, int numberOfItem) {
		(new Thread(new ConcurrentTest(svrName, customerID, itemID, numberOfItem))).start();
	}


}
