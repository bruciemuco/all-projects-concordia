package common;

import java.io.IOException;

import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import ca.com.Concordia.distributed.Ass_2.DRS_Client.MyServiceClientImpl;

import DRSCommon.DRSClientCommon;
import DRSCommon.DRSCommonService;
import DRS2Common.*;

public class ClientCommon implements DRSServices {

	DRSCommonService svr = null;

	ClientCommon(String RMName, String customID) {
		try {			
			if (RMName.equals(Conf.RM_NAME_DRS1)) {
				// implementation of DRS 1 (developed by Yuan Tao)
				if (customID == null) {
					svr = new DRSClientCommon(SvrInfo.SVR_NAME_MONTREAL)
						.getORBInterface();
					return;
				}
			
				if (customID.charAt(0) == 'M') {
					svr = new DRSClientCommon(SvrInfo.SVR_NAME_MONTREAL)
							.getORBInterface();
				} else if (customID.charAt(0) == 'T') {
					svr = new DRSClientCommon(SvrInfo.SVR_NAME_TORONTO)
							.getORBInterface();
				} else {
					svr = new DRSClientCommon(SvrInfo.SVR_NAME_VANCOUVER)
							.getORBInterface();
				}
				
			} else if (RMName.equals(Conf.RM_NAME_DRS2)) {
				// implementation of DRS 2 (developed by Yichen Li)
				if (customID == null) {
					svr = new DRSClientImpl().tgt[0];
					return;
				}
				if (customID.charAt(0) == 'M') {
					svr = new DRSClientImpl().tgt[0];
				} else if (customID.charAt(0) == 'T') {
					svr = new DRSClientImpl().tgt[1];
				} else {
					svr = new DRSClientImpl().tgt[2];
				}
				
			} else {
				// implementation of DRS 3 (developed by Xiaodong Zhang)
				if (customID == null) {
					svr = new MyServiceClientImpl(SvrInfo.SVR3_NAME_MONTREAL)
						.getORBInterface(0);
					return;
				}
			
				if (customID.charAt(0) == 'M') {
					svr = new MyServiceClientImpl(SvrInfo.SVR3_NAME_MONTREAL)
							.getORBInterface(0);
				} else if (customID.charAt(0) == 'T') {
					svr = new MyServiceClientImpl(SvrInfo.SVR3_NAME_TORONTO)
							.getORBInterface(0);
				} else {
					svr = new MyServiceClientImpl(SvrInfo.SVR3_NAME_VANCOUVER)
							.getORBInterface(0);
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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
