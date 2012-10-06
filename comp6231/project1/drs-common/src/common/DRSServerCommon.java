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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;


/*
 * A common class used by all the servers
 * 
 */
public class DRSServerCommon {
	
	// create a hash table to store the items <itemID, number_available>
	private Hashtable<String, Integer> htItems = new Hashtable<String, Integer>();
	
	// hash table of <UserID, ItemID, NumberOfItem> for storing the items info of the users.
	private Hashtable<String, Hashtable<String, Integer>> htUsers = new Hashtable<String, Hashtable<String, Integer>>();
	
	private String svrName;

	// initialize the store
	public int init(String name) {
		// initialize SysLogger
		SysLogger.init();

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
		
		return 0;
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
			SysLogger.info("saveUserInfo2File: " + userID);
			
		} catch (Exception e) {
			StringWriter err = new StringWriter();
			e.printStackTrace(new PrintWriter(err));
			SysLogger.err(err.toString());
		}
		return 0;
	}
	
	public boolean ifValidCustomerID(String customerID) {
		if (customerID.length() != 6) {
			return false;
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
	
	public int buy(String customerID, String itemID, int numberOfItem) {
		if (!ifValidCustomerID(customerID) || !ifValidItemID(itemID) || numberOfItem < 1) {
			SysLogger.err("buy: Invalid params. " + customerID + ", " + itemID + ", " + numberOfItem);
			return 1;
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
		
		return 1;
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
		
		SysLogger.info("buy: " + customerID + ", " + itemID + ", " + numberOfItem
				+ ". Num Before: " + num);
		return 0;
	}
	
	public String checkStock(String itemID) {
		if (!ifValidItemID(itemID)) {
			SysLogger.err("checkStock: Invalid params. " + itemID);
			return "ERROR. Invalid itemID";
		}
		
		// get the number of item from local store		
		if (htItems.get(itemID) == null) {
			// program should not go here
			SysLogger.err("returnNumOfItem: itemID not exit");
			htItems.put(itemID, 0);
		}
		
		String ret = svrName + ": " + htItems.get(itemID);
		
		// try to get the number from remote servers
		
		return ret;
	}
}
