/*
 * COMP6231 Project
 * 
 * SysLogger
 * 
 * This file is created by Yuan Tao (ewan.msn@gmail.com)
 * Licensed under GNU GPL v3
 * 
 * $Author: ewan.msn@gmail.com $
 * $Date: 2012-07-13 21:59:51 -0400 (Fri, 13 Jul 2012) $
 * $Rev: 112 $
 * $HeadURL: https://all-projects-concordia.googlecode.com/svn/soen6611/MinskPrjD1/src/utils/SysLogger.java $
 * 
 */

package client;

import java.rmi.Naming;
import java.rmi.RMISecurityManager;

import common.DRSCommon;
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
			DRSCommon svr = (DRSCommon) Naming.lookup("rmi://localhost/DRSServer_Montreal");
			
			String ret = svr.checkStock("1011");
			System.out.println("ret: " + ret);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
