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

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DRSCommon extends Remote {

	public int buy(String customerID, String itemID, int numberOfItem) throws RemoteException;
	public int returnNumOfItem(String customerID, String itemID, int numberOfItem) throws RemoteException;
	public String checkStock(String itemID) throws RemoteException;
}
