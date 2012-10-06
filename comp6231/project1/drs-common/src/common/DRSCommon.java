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

package common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DRSCommon extends Remote {

	public int buy(String customerID, String itemID, int numberOfItem) throws RemoteException;
	public int returnNumOfItem(String customerID, String itemID, int numberOfItem) throws RemoteException;
	public String checkStock(String itemID) throws RemoteException;
}
