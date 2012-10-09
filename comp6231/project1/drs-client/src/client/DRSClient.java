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

import java.rmi.Naming;
import java.rmi.RMISecurityManager;

import common.DRSCommon;
import common.SvrInfo;
import common.SysLogger;

public class DRSClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// initialize SysLogger
		SysLogger.init();

		try {
			System.setSecurityManager(new RMISecurityManager());
			DRSCommon svr = (DRSCommon) Naming.lookup("rmi://localhost/" + SvrInfo.SVR_NAME_TORONTO);
			
			String customerID = "A100001";
			String itemID = "1101";
			String ret = svr.checkStock(itemID);
			SysLogger.info("checkStock " + itemID + ": " + ret);
			
			int buyRet = -1;
			int numberOfItem = 10;
			buyRet = svr.buy(customerID, itemID, numberOfItem);
			if (buyRet != 0) {
				SysLogger.info("buy successfully. " + customerID + ", " + itemID + ", " + numberOfItem);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
