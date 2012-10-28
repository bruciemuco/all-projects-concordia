package server;
import common.DRSServerCommon;
import common.SvrInfo;

/**
 * This class is the implemetation object for your IDL interface.
 *
 * Let the Eclipse complete operations code by choosing 'Add unimplemented methods'.
 */
public class DRSCommonServiceServerImpl extends DRSCommon.DRSCommonServicePOA {
	/**
	 * Constructor for DRSCommonServiceServerImpl 
	 */
	private DRSServerCommon svr = new DRSServerCommon();
	public String svrName;
	
	public DRSCommonServiceServerImpl(String name) {
		int port = 0;

		if (name.equals(SvrInfo.SVR_NAME_MONTREAL)) {
			port = SvrInfo.SVR_PORT_MONTREAL;
		} else if (name.equals(SvrInfo.SVR_NAME_TORONTO)) {
			port = SvrInfo.SVR_PORT_TORONTO;
		} else {
			port = SvrInfo.SVR_PORT_VANCOUVER;
		}
		svr.init(name, port);
		svrName = name;
	}

	@Override
	public int buy(String customerID, String itemID, int numberOfItem) {
		return svr.buy(customerID, itemID, numberOfItem);
	}

	@Override
	public int returnNumOfItem(String customerID, String itemID,
			int numberOfItem) {
		return svr.returnNumOfItem(customerID, itemID, numberOfItem);
	}

	@Override
	public String checkStock(String itemID) {
		return svr.checkStock(itemID);
	}

	@Override
	public int exchange(String customerID, String boughtItemID,
			int boughtNumber, String desiredItemID, int desiredNumber) {
		return svr.exchange(customerID, boughtItemID, boughtNumber, desiredItemID, desiredNumber);
	}
}
