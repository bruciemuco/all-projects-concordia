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

import common.DRSCommon;
import common.DRSServerCommon;
import common.SvrInfo;
import common.SysLogger;

public class DRSClient {

	public static void main(String[] args) {
		// initialize SysLogger
		SysLogger.init();
		
		try {
			// simple test
			// one customer, one item, three stores
			String svrName = SvrInfo.SVR_NAME_MONTREAL;
			String customerID = "M10001";
			String itemID = DRSServerCommon.TEST_ITEMID;
			int numberOfItem = 10;
			
			ConcurrentTest t = new ConcurrentTest(svrName, customerID, itemID, numberOfItem);
			t.simpleTest(svrName, customerID, itemID, numberOfItem);

//			t = new ConcurrentTest();
//			t.svrName = SvrInfo.SVR_NAME_TORONTO;
//			t.simpleTest();

			// concurrent test 
			// three users concurrently accessing one item
			itemID = DRSServerCommon.TEST_ITEMID_CON;
			
			ConcurrentTest.concurrentTest(SvrInfo.SVR_NAME_MONTREAL, "M10001", itemID, numberOfItem);
			ConcurrentTest.concurrentTest(SvrInfo.SVR_NAME_TORONTO, "T10001", itemID, numberOfItem);
			ConcurrentTest.concurrentTest(SvrInfo.SVR_NAME_VANCOUVER, "V10001", itemID, numberOfItem);
			
			Thread.sleep(10*1000);
		} catch (Exception e) {
			StringWriter err = new StringWriter();
			e.printStackTrace(new PrintWriter(err));
			SysLogger.err(err.toString());
		}

	}
}
