package ca.com.Concordia.distributed.Ass_2.DRS_ServerVAN;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Item {

	String itemID;
	ArrayList<String> list = new ArrayList<String>();

	public Item(String itemID) {
		this.itemID = itemID;
		list = readFile();
	}

	public int getAvailability() {
		int count = 0;
		for (int i = 0; i < list.size(); i++) {
			String customerInfo = list.get(i);
			count += Integer.parseInt(customerInfo.split(" ")[1]);
		}
		return 20 - count;
	}

	public String purchase(int customerID, String itemID, int numberOfItem)
    {
		if (getAvailability() < numberOfItem)
			return "Not enough avalability";
		if (isPurchased(customerID) == null) {
			list.add(customerID + " " + numberOfItem);
		} else {
			String str = "";
			for (int i = 0; i < list.size(); i++) {
				if (Integer.parseInt(list.get(i).split(" ")[0]) == customerID) {
					Integer a = Integer.parseInt(list.get(i).split(" ")[1])
							+ numberOfItem;
					str = list.get(i).split(" ")[0] + " " + a;
					list.remove(i);
				}
			}
			list.add(str);
		}
		try {
			writeToFile(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "Purchase Success!";
	}

	public String giveback(int customerID, String itemID, int numberOfItem)
    {
		if (numberOfItem <= 0)
			return "Item number has to be greater than 1";
		String purchase = isPurchased(customerID);
		if (purchase == null) {
			return "You have no purchase";
		} else {
			Integer itemNumber = Integer.parseInt(purchase.split(" ")[2]);
			if (itemNumber < numberOfItem)
				return "Your purchase information is: " + purchase
						+ "\nYou don't have " + numberOfItem + " items!!";
			else {
				if (itemNumber == numberOfItem) {
					for (int i = 0; i < list.size(); i++) {
						if (Integer.parseInt(list.get(i).split(" ")[0]) == customerID)
							list.remove(i);
					}
				} else {
					String str = "";
					for (int i = 0; i < list.size(); i++) {
						if (Integer.parseInt(list.get(i).split(" ")[0]) == customerID) {
							Integer a = Integer.parseInt(list.get(i).split(" ")[1]) - numberOfItem;
							str = list.get(i).split(" ")[0] + " " + a;
							list.remove(i);
						}
					}
					list.add(str);
				}
				try {
					writeToFile(list);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return "Success!\ngiveback your purchase " + purchase;
			}
		}

	}
	public String isPurchased(int customerID)
	{
		for(int i=0;i<list.size();i++)
		{
			if(Integer.parseInt(list.get(i).split(" ")[0])==customerID)
			{
				return itemID+" "+list.get(i);
			}
		}
		return null;
	}
	public void writeToFile(ArrayList<String> infoList) throws FileNotFoundException, IOException 
	{
		String content="";
		for(int i=0;i<infoList.size();i++)
		{
			content+=infoList.get(i)+"\n";
		}
		File f=new File(itemID);
		BufferedWriter output = new BufferedWriter(new FileWriter(f));
		try{
			output.write(content);
		}
		finally{
			output.close();
		}
	}
	public ArrayList<String> readFile() {
		ArrayList<String> info = new ArrayList<String>();
		try {
			String line = null;
			BufferedReader input = new BufferedReader(new FileReader(itemID));

			while ((line = input.readLine()) != null) {
				info.add(line);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return info;
	}

}

