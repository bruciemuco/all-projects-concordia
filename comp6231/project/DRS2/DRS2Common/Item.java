package DRS2Common;
 /* 
 * COMP6231-Assignment2-CORBA
 * Author: YICHEN LI
 * Student ID: 6389635
 */ 
public class Item {

	private String itemID;
	private int numberOfItem;
	private int index;
	
	
	public Item(String itemID, int numberOfItem, int index)
	{
		this.itemID = itemID;
		this.numberOfItem = numberOfItem;
		this.index = index;
	}
	
	
	
	public String getItemID() {
		return itemID;
	}

	public void setItemID(String itemID) {
		this.itemID = itemID;
	}

	public int getNumberOfItem() {
		return numberOfItem;
	}

	public void setNumberOfItem(int numberOfItem) {
		this.numberOfItem = numberOfItem;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	
	

	
	
}
