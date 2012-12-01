/*
 * COMP6231 Project
 * 
 * This file is created by Yuan Tao (ewan.msn@gmail.com)
 * Licensed under GNU GPL v3
 * 
 * $Author: ewan.msn@gmail.com $
 * $Date: 2012-10-28 00:12:47 -0400 (Sun, 28 Oct 2012) $
 * $Rev: 181 $
 * $HeadURL: https://all-projects-concordia.googlecode.com/svn/comp6231/assignment2/src/common/DRSServerCommon.java $
 * 
 */

package DRSCommon;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;

import common.SvrInfo;
import common.SysLogger;



/*
 * A common class used by all the servers
 * 
 */
public class DRSServerCommon {
	
	// create a hash table to store the items <itemID, number_available>
	// Hashtable is thread safe
	private Hashtable<String, Integer> htItems = new Hashtable<String, Integer>();
	
	// hash table of <UserID, ItemID, NumberOfItem> for storing the items info of the users.
	private Hashtable<String, Hashtable<String, Integer>> htUsers = new Hashtable<String, Hashtable<String, Integer>>();
	
	private String svrName;
	
	private UDPLibs udpLibs = new UDPLibs(0, null);
	
	public static final String TEST_ITEMID = "1234";
	public static final String TEST_ITEMID_EXCHG = "1235";
	public static final String TEST_ITEMID_CON = "4321";
	
	// initialize the store
	public int init(String name, int localSvrPort) {
		// initialize SysLogger
		//SysLogger.init();

		svrName = name;
		
		// Generate random items
		Random rd = new Random(System.nanoTime());
		
		int i = 0;
		while (i++ < 10000) {
			String key = "";
			if (i < 10) {
				key = "000" + i;
			} else if (i < 100) {
				key = "00" + i;
			} else if (i < 1000) {
				key = "0" + i;
			} else if (i < 10000) {
				key = "" + i;
			}
			int value = Math.abs(rd.nextInt() % 100);
			htItems.put(key, value);
		}
		
		if (svrName.equals(SvrInfo.SVR_NAME_MONTREAL) 
				|| svrName.equals(SvrInfo.SVR2_NAME_MONTREAL) 
				|| svrName.equals(SvrInfo.SVR3_NAME_MONTREAL)) {
			htItems.put(TEST_ITEMID, 20);
		} else {
			htItems.put(TEST_ITEMID, 10);
		}
		
		htItems.put(TEST_ITEMID_EXCHG, 10);
		htItems.put(TEST_ITEMID_CON, 15);
		
		// start UDP server
		udpLibs.svr = this;
		//udpLibs.udpLocalSvrPort = localSvrPort;
		udpLibs.udpServerStart(localSvrPort);
		
		// TODO: use semaphore to check if the thread has already started
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			StringWriter err = new StringWriter();
			e.printStackTrace(new PrintWriter(err));
			SysLogger.err(err.toString());
		}
		return 0;
	}
	
	public void reloadData(String data) {
		// TODO
	}
	public String getData() {
		return TEST_ITEMID + "," + htItems.get(TEST_ITEMID) + ","
				+ TEST_ITEMID_EXCHG + "," + htItems.get(TEST_ITEMID_EXCHG) + ","
				+ TEST_ITEMID_CON + "," + htItems.get(TEST_ITEMID_CON) + ",";
	}
	
	public void exit() {
		// UDPLibs.bExitThread = true;
		// TODO: send a packet
		//udpLibs.udpCheckOtherStock("2233", SvrInfo.SVR_PORT_MONTREAL);
	}

	// save the items and their numbers of the user into the corresponding file.
	private int saveUserInfo2File(String userID) {
		String fileFullname = System.getProperty("user.dir") + "\\output\\" + userID + ".txt";
		
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(fileFullname));
			Hashtable<String, Integer> items = htUsers.get(userID);
			
			Enumeration<String> eKey = items.keys();
			while (eKey.hasMoreElements()) {
				String itemID = (String) eKey.nextElement();
				Integer numOfItem = items.get(itemID);
				bw.write(itemID + ", " + numOfItem + "\r\n");
			}
			bw.close();
			//SysLogger.info("saveUserInfo2File: " + userID);
			
		} catch (Exception e) {
			StringWriter err = new StringWriter();
			e.printStackTrace(new PrintWriter(err));
			SysLogger.err(err.toString());
		}
		return 0;
	}
	
	public boolean ifValidCustomerID(String customerID) {
		if (customerID.length() != 6) {
			if (customerID.length() == 7) { 
				customerID = customerID.substring(1, 7);
			} else {
				return false;
			}
		}
		
		char ch = customerID.charAt(0);
		if (!((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z'))) {
			return false;
		}
		
		int i = 0;
		while (i++ < 5) {
			ch = customerID.charAt(i);
			if (ch < '0' || ch > '9') {
				return false;
			}
		}
		
		return true;
	}
	public boolean ifValidItemID(String itemID) {
		if (itemID.length() != 4) {
			return false;
		}
		
		int i = -1;
		while (i++ < 3) {
			char ch = itemID.charAt(i);
			if (ch < '0' || ch > '9') {
				return false;
			}
		}
		
		return true;
	}
	
	// try to buy the items from other stores
	public int buyFromOtherServers(String customerID, String itemID, int numberOfItem) {
		// change the customerID to indicate that it is a server request
		if ((customerID.length() == 7)) {  	// TODO: need a better way 
			return -1;		// avoid the loop
		}
		if (svrName.equals(SvrInfo.SVR_NAME_MONTREAL) 
				|| svrName.equals(SvrInfo.SVR2_NAME_MONTREAL) 
				|| svrName.equals(SvrInfo.SVR3_NAME_MONTREAL)) {
			customerID = 'M' + customerID;
		}
		if (svrName.equals(SvrInfo.SVR_NAME_TORONTO) 
				|| svrName.equals(SvrInfo.SVR2_NAME_TORONTO) 
				|| svrName.equals(SvrInfo.SVR3_NAME_TORONTO)) {
			customerID = 'T' + customerID;
		}
		if (svrName.equals(SvrInfo.SVR_NAME_VANCOUVER) 
				|| svrName.equals(SvrInfo.SVR2_NAME_VANCOUVER) 
				|| svrName.equals(SvrInfo.SVR3_NAME_VANCOUVER)) {
			customerID = 'V' + customerID;
		}
		
		try {
			int ret = 0;
			DRSCommonService svr = null;
			
			if (svrName.equals(SvrInfo.SVR_NAME_MONTREAL)) {
				svr = new DRSClientCommon(SvrInfo.SVR_NAME_TORONTO).getORBInterface();
				ret = svr.buy(customerID, itemID, numberOfItem);
				if (ret != 0) {
					svr = new DRSClientCommon(SvrInfo.SVR_NAME_VANCOUVER).getORBInterface();
					ret = svr.buy(customerID, itemID, numberOfItem);
				}
				if (ret != 0) {
					return -1;
				}
				return ret;
				
			} else if (svrName.equals(SvrInfo.SVR_NAME_TORONTO)) {
				svr = new DRSClientCommon(SvrInfo.SVR_NAME_MONTREAL).getORBInterface();
				ret = svr.buy(customerID, itemID, numberOfItem);
				if (ret != 0) {
					svr = new DRSClientCommon(SvrInfo.SVR_NAME_VANCOUVER).getORBInterface();
					ret = svr.buy(customerID, itemID, numberOfItem);
				}
				if (ret != 0) {
					return -1;
				}
				return ret;
				
			} else if (svrName.equals(SvrInfo.SVR_NAME_VANCOUVER)) {
				svr = new DRSClientCommon(SvrInfo.SVR_NAME_MONTREAL).getORBInterface();
				ret = svr.buy(customerID, itemID, numberOfItem);
				if (ret != 0) {
					svr = new DRSClientCommon(SvrInfo.SVR_NAME_TORONTO).getORBInterface();
					ret = svr.buy(customerID, itemID, numberOfItem);
				}
				if (ret != 0) {
					return -1;
				}
				return ret;
			} else if (svrName.equals(SvrInfo.SVR2_NAME_MONTREAL)) {
				svr = new DRSClientCommon(SvrInfo.SVR2_NAME_TORONTO).getORBInterface();
				ret = svr.buy(customerID, itemID, numberOfItem);
				if (ret != 0) {
					svr = new DRSClientCommon(SvrInfo.SVR2_NAME_VANCOUVER).getORBInterface();
					ret = svr.buy(customerID, itemID, numberOfItem);
				}
				if (ret != 0) {
					return -1;
				}
				return ret;
				
			} else if (svrName.equals(SvrInfo.SVR2_NAME_TORONTO)) {
				svr = new DRSClientCommon(SvrInfo.SVR2_NAME_MONTREAL).getORBInterface();
				ret = svr.buy(customerID, itemID, numberOfItem);
				if (ret != 0) {
					svr = new DRSClientCommon(SvrInfo.SVR2_NAME_VANCOUVER).getORBInterface();
					ret = svr.buy(customerID, itemID, numberOfItem);
				}
				if (ret != 0) {
					return -1;
				}
				return ret;
				
			} else if (svrName.equals(SvrInfo.SVR2_NAME_VANCOUVER)) {
				svr = new DRSClientCommon(SvrInfo.SVR2_NAME_MONTREAL).getORBInterface();
				ret = svr.buy(customerID, itemID, numberOfItem);
				if (ret != 0) {
					svr = new DRSClientCommon(SvrInfo.SVR2_NAME_TORONTO).getORBInterface();
					ret = svr.buy(customerID, itemID, numberOfItem);
				}
				if (ret != 0) {
					return -1;
				}
				return ret;
			} else if (svrName.equals(SvrInfo.SVR3_NAME_MONTREAL)) {
				svr = new DRSClientCommon(SvrInfo.SVR3_NAME_TORONTO).getORBInterface();
				ret = svr.buy(customerID, itemID, numberOfItem);
				if (ret != 0) {
					svr = new DRSClientCommon(SvrInfo.SVR3_NAME_VANCOUVER).getORBInterface();
					ret = svr.buy(customerID, itemID, numberOfItem);
				}
				if (ret != 0) {
					return -1;
				}
				return ret;
				
			} else if (svrName.equals(SvrInfo.SVR3_NAME_TORONTO)) {
				svr = new DRSClientCommon(SvrInfo.SVR3_NAME_MONTREAL).getORBInterface();
				ret = svr.buy(customerID, itemID, numberOfItem);
				if (ret != 0) {
					svr = new DRSClientCommon(SvrInfo.SVR3_NAME_VANCOUVER).getORBInterface();
					ret = svr.buy(customerID, itemID, numberOfItem);
				}
				if (ret != 0) {
					return -1;
				}
				return ret;
				
			} else if (svrName.equals(SvrInfo.SVR3_NAME_VANCOUVER)) {
				svr = new DRSClientCommon(SvrInfo.SVR3_NAME_MONTREAL).getORBInterface();
				ret = svr.buy(customerID, itemID, numberOfItem);
				if (ret != 0) {
					svr = new DRSClientCommon(SvrInfo.SVR3_NAME_TORONTO).getORBInterface();
					ret = svr.buy(customerID, itemID, numberOfItem);
				}
				if (ret != 0) {
					return -1;
				}
				return ret;
			}

		} catch (Exception e) {
			StringWriter err = new StringWriter();
			e.printStackTrace(new PrintWriter(err));
			SysLogger.err(err.toString());
			return -1;
		}
		
		return 0;
	}
	
	public int buy(String customerID, String itemID, int numberOfItem) {
		if (!ifValidCustomerID(customerID) || !ifValidItemID(itemID) || numberOfItem < 1) {
			SysLogger.err("buy: Invalid params. " + customerID + ", " + itemID + ", " + numberOfItem);
			return -1;
		}
		
		if (htItems.get(itemID) != null) {
			int num = htItems.get(itemID);
			if (num >= numberOfItem) {
				// item is available
				// first decrease the number of item from the item list
				htItems.put(itemID, num - numberOfItem);
				
				// then update the user info in memory
				Hashtable<String, Integer> items = htUsers.get(customerID);
				if (items == null) {
					items = new Hashtable<String, Integer>();
				}

				int numOfItemBought = 0;
				if (items.get(itemID) != null) {
					numOfItemBought = items.get(itemID); 
				}
				items.put(itemID, numOfItemBought + numberOfItem);
				htUsers.put(customerID, items);
				
				// update user info in file
				saveUserInfo2File(customerID);
				
				SysLogger.info("buy: " + customerID + ", " + itemID + ", " + numberOfItem
						+ ". Num Before: " + num);
				return 0;
			}
		}
		
		// the required item is not available at this store, try to buy them from other stores
		return buyFromOtherServers(customerID, itemID, numberOfItem);
	}
	
	public int returnNumOfItem(String customerID, String itemID, int numberOfItem) {
		if (!ifValidCustomerID(customerID) || !ifValidItemID(itemID) || numberOfItem < 1) {
			SysLogger.err("returnNumOfItem: Invalid params. " + customerID + ", " + itemID + ", " + numberOfItem);
			return 1;
		}

		if (htItems.get(itemID) == null) {
			// program should not go here
			SysLogger.err("returnNumOfItem: itemID not exit");
			htItems.put(itemID, 0);
		}
		
		int num = htItems.get(itemID);
		
		// increase the number of item to the item list
		htItems.put(itemID, num + numberOfItem);
		
		// update user info in memory
		Hashtable<String, Integer> items = htUsers.get(customerID);
		if (items == null) {
			items = new Hashtable<String, Integer>();
		}

		int numOfItemBought = 0;
		if (items.get(itemID) != null) {
			numOfItemBought = items.get(itemID); 
		}
		items.put(itemID, numOfItemBought - numberOfItem); // it might be a negative number
		htUsers.put(customerID, items);
		
		// update user info in file
		saveUserInfo2File(customerID);
		
		SysLogger.info("return: " + customerID + ", " + itemID + ", " + numberOfItem
				+ ". Num Before: " + num);
		return 0;
	}
	
	// get the number of available item by itemID in the local stock
	public int checkLocalStock(String itemID) {
		if (!ifValidItemID(itemID)) {
			SysLogger.err("checkStock: Invalid params. " + itemID);
			return -1;
		}
		
		// get the number of item from local store		
		if (htItems.get(itemID) == null) {
			// program should not go here
			SysLogger.err("returnNumOfItem: itemID not exit");
			htItems.put(itemID, 0);
		}
		
		return htItems.get(itemID);
	}
	
	// get num of items from other stores
	private String checkOtherStocks(String itemID) {
		String ret = "";
		
		if (svrName.equals(SvrInfo.SVR_NAME_MONTREAL)) {
			String remoteRet = udpLibs.udpCheckOtherStock(itemID, SvrInfo.SVR_PORT_TORONTO);
			//SysLogger.info("udpCheckOtherStock from Toronto: " + remoteRet);
			ret += SvrInfo.SVR_NAME_TORONTO.substring(5) + "=" + remoteRet + ";";
			
			remoteRet = udpLibs.udpCheckOtherStock(itemID, SvrInfo.SVR_PORT_VANCOUVER);
			//SysLogger.info("udpCheckOtherStock from Vancouver: " + remoteRet);
			ret += SvrInfo.SVR_NAME_VANCOUVER.substring(5) + "=" + remoteRet + ";";
			
		} else if (svrName.equals(SvrInfo.SVR_NAME_TORONTO)) {
			String remoteRet = udpLibs.udpCheckOtherStock(itemID, SvrInfo.SVR_PORT_MONTREAL);
			ret += SvrInfo.SVR_NAME_MONTREAL.substring(5) + "=" + remoteRet + ";";
			
			remoteRet = udpLibs.udpCheckOtherStock(itemID, SvrInfo.SVR_PORT_VANCOUVER);
			ret += SvrInfo.SVR_NAME_VANCOUVER.substring(5) + "=" + remoteRet + ";";
			
		} else if (svrName.equals(SvrInfo.SVR_NAME_VANCOUVER)) {
			String remoteRet = udpLibs.udpCheckOtherStock(itemID, SvrInfo.SVR_PORT_MONTREAL);
			ret += SvrInfo.SVR_NAME_MONTREAL.substring(5) + "=" + remoteRet + ";";
			
			remoteRet = udpLibs.udpCheckOtherStock(itemID, SvrInfo.SVR_PORT_TORONTO);
			ret += SvrInfo.SVR_NAME_TORONTO.substring(5) + "=" + remoteRet + ";";
			
		} else 	if (svrName.equals(SvrInfo.SVR2_NAME_MONTREAL)) {
			String remoteRet = udpLibs.udpCheckOtherStock(itemID, SvrInfo.SVR2_PORT_TORONTO);
			//SysLogger.info("udpCheckOtherStock from Toronto: " + remoteRet);
			ret += SvrInfo.SVR2_NAME_TORONTO.substring(5) + "=" + remoteRet + ";";
			
			remoteRet = udpLibs.udpCheckOtherStock(itemID, SvrInfo.SVR2_PORT_VANCOUVER);
			//SysLogger.info("udpCheckOtherStock from Vancouver: " + remoteRet);
			ret += SvrInfo.SVR2_NAME_VANCOUVER.substring(5) + "=" + remoteRet + ";";
			
		} else if (svrName.equals(SvrInfo.SVR2_NAME_TORONTO)) {
			String remoteRet = udpLibs.udpCheckOtherStock(itemID, SvrInfo.SVR2_PORT_MONTREAL);
			ret += SvrInfo.SVR2_NAME_MONTREAL.substring(5) + "=" + remoteRet + ";";
			
			remoteRet = udpLibs.udpCheckOtherStock(itemID, SvrInfo.SVR2_PORT_VANCOUVER);
			ret += SvrInfo.SVR2_NAME_VANCOUVER.substring(5) + "=" + remoteRet + ";";
			
		} else if (svrName.equals(SvrInfo.SVR2_NAME_VANCOUVER)) {
			String remoteRet = udpLibs.udpCheckOtherStock(itemID, SvrInfo.SVR2_PORT_MONTREAL);
			ret += SvrInfo.SVR2_NAME_MONTREAL.substring(5) + "=" + remoteRet + ";";
			
			remoteRet = udpLibs.udpCheckOtherStock(itemID, SvrInfo.SVR2_PORT_TORONTO);
			ret += SvrInfo.SVR2_NAME_TORONTO.substring(5) + "=" + remoteRet + ";";
			
		} else 	if (svrName.equals(SvrInfo.SVR3_NAME_MONTREAL)) {
			String remoteRet = udpLibs.udpCheckOtherStock(itemID, SvrInfo.SVR3_PORT_TORONTO);
			//SysLogger.info("udpCheckOtherStock from Toronto: " + remoteRet);
			ret += SvrInfo.SVR3_NAME_TORONTO.substring(5) + "=" + remoteRet + ";";
			
			remoteRet = udpLibs.udpCheckOtherStock(itemID, SvrInfo.SVR3_PORT_VANCOUVER);
			//SysLogger.info("udpCheckOtherStock from Vancouver: " + remoteRet);
			ret += SvrInfo.SVR3_NAME_VANCOUVER.substring(5) + "=" + remoteRet + ";";
			
		} else if (svrName.equals(SvrInfo.SVR3_NAME_TORONTO)) {
			String remoteRet = udpLibs.udpCheckOtherStock(itemID, SvrInfo.SVR3_PORT_MONTREAL);
			ret += SvrInfo.SVR3_NAME_MONTREAL.substring(5) + "=" + remoteRet + ";";
			
			remoteRet = udpLibs.udpCheckOtherStock(itemID, SvrInfo.SVR3_PORT_VANCOUVER);
			ret += SvrInfo.SVR3_NAME_VANCOUVER.substring(5) + "=" + remoteRet + ";";
			
		} else if (svrName.equals(SvrInfo.SVR3_NAME_VANCOUVER)) {
			String remoteRet = udpLibs.udpCheckOtherStock(itemID, SvrInfo.SVR3_PORT_MONTREAL);
			ret += SvrInfo.SVR3_NAME_MONTREAL.substring(5) + "=" + remoteRet + ";";
			
			remoteRet = udpLibs.udpCheckOtherStock(itemID, SvrInfo.SVR3_PORT_TORONTO);
			ret += SvrInfo.SVR3_NAME_TORONTO.substring(5) + "=" + remoteRet + ";";
		}
		return ret;
	}
	
	public String checkStock(String itemID) {
		int numLocal = checkLocalStock(itemID); 
		if (numLocal == -1) {
			return "ERROR. Invalid itemID";
		}
		
		String ret = svrName.substring(5) + "=" + numLocal + ";";
		
		// try to get the number from remote servers
		return ret + checkOtherStocks(itemID);
	}
	
	public int exchange(String customerID, String boughtItemID,
			int boughtNumber, String desiredItemID, int desiredNumber) {
		
		Hashtable<String, Integer> items = htUsers.get(customerID);
		if (items == null) {
			return 0;		// the user does not buy anything.
		}
		if (items.get(boughtItemID) == null) {
			return 0;		// no such item
		}
		if (items.get(boughtItemID) < boughtNumber) {
			return 0;		// not enough item
		}

		if (buy(customerID, desiredItemID, desiredNumber) == 0) {
			returnNumOfItem(customerID, boughtItemID, boughtNumber);
			return 1;
		}
		
		return 0;
	}
}
