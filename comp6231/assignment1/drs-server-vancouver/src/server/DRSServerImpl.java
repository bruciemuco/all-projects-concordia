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

package server;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import common.DRSCommon;
import common.DRSServerCommon;
import common.SvrInfo;
import common.SysLogger;

public class DRSServerImpl extends UnicastRemoteObject implements DRSCommon {
	private DRSServerCommon svr = new DRSServerCommon();
	
	protected DRSServerImpl(String name) throws RemoteException {
		svr.init(name, SvrInfo.SVR_PORT_VANCOUVER);
	}

	@Override
	public int buy(String customerID, String itemID, int numberOfItem)
			throws RemoteException {
		return svr.buy(customerID, itemID, numberOfItem);
	}

	@Override
	public int returnNumOfItem(String customerID, String itemID,
			int numberOfItem) throws RemoteException {
		return svr.returnNumOfItem(customerID, itemID, numberOfItem);
	}

	@Override
	public String checkStock(String itemID) throws RemoteException {
		return svr.checkStock(itemID);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			Registry reg = LocateRegistry.getRegistry();
			String name = SvrInfo.SVR_NAME_VANCOUVER;
			reg.rebind(name, new DRSServerImpl(name));
			SysLogger.info(name + " is ready!");
			
//			name = "DRSServer_Montreal_2";
//			reg.rebind(name, new DRSServerImpl(name));
//			SysLogger.info(name + " is ready!");
//
//			name = "DRSServer_Montreal_3";
//			reg.rebind(name, new DRSServerImpl(name));
//			SysLogger.info(name + " is ready!");

		} catch (Exception e) {
			StringWriter err = new StringWriter();
			e.printStackTrace(new PrintWriter(err));
			SysLogger.err(err.toString());
		}
		
		
	}

}
