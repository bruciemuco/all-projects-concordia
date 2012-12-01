package common;

public interface DRSServices {
	int buy(String customerID, String itemID, int numberOfItem);

	int returnNumOfItem(String customerID, String itemID, int numberOfItem);

	String checkStock(String itemID);

	int exchange(String customerID, String boughtItemID, int boughtNumber,
			String desiredItemID, int desiredNumber);

}
