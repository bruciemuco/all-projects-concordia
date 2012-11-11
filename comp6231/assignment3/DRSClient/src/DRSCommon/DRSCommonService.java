/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DRSCommon;

/**
 *
 * @author Ewan
 */
public class DRSCommonService {
    public static final String TEST_ITEMID = "1234";
    public static final String TEST_ITEMID_CON = "4321";
    
    String svrName;
    ws.Montreal mport = null;
    ws.Toronto tport = null;
    ws.Vancouver vport = null;
      
    public DRSCommonService(String name) {
        svrName = name;

        if (svrName.equals(SvrInfo.SVR_NAME_MONTREAL)) {
            ws.Montreal_Service service = new ws.Montreal_Service();
            mport = service.getMontrealPort();
        } else if (svrName.equals(SvrInfo.SVR_NAME_TORONTO)) {
            ws.Toronto_Service service = new ws.Toronto_Service();
            tport = service.getTorontoPort();
        } else if (svrName.equals(SvrInfo.SVR_NAME_VANCOUVER)) {
            ws.Vancouver_Service service = new ws.Vancouver_Service();
            vport = service.getVancouverPort();
        }
    }

    public int buy(java.lang.String customerID, java.lang.String itemID, int numberOfItem) {
        if (svrName.equals(SvrInfo.SVR_NAME_MONTREAL)) {
            return mport.buy(customerID, itemID, numberOfItem);
        } else if (svrName.equals(SvrInfo.SVR_NAME_TORONTO)) {
            return tport.buy(customerID, itemID, numberOfItem);
        }         
        return vport.buy(customerID, itemID, numberOfItem);
    }

    public String checkStock(java.lang.String itemID) {
        if (svrName.equals(SvrInfo.SVR_NAME_MONTREAL)) {
            return mport.checkStock(itemID);
        } else if (svrName.equals(SvrInfo.SVR_NAME_TORONTO)) {
            return tport.checkStock(itemID);
        }         
        return vport.checkStock(itemID);
    }

    public int exchange(java.lang.String customerID, java.lang.String boughtItemID, int boughtNumber, java.lang.String desiredItemID, int desiredNumber) {
        if (svrName.equals(SvrInfo.SVR_NAME_MONTREAL)) {
            return mport.exchange(customerID, boughtItemID, boughtNumber, desiredItemID, desiredNumber);
        } else if (svrName.equals(SvrInfo.SVR_NAME_TORONTO)) {
            return tport.exchange(customerID, boughtItemID, boughtNumber, desiredItemID, desiredNumber);
        }         
        return vport.exchange(customerID, boughtItemID, boughtNumber, desiredItemID, desiredNumber);
    }

    public int returnNumOfItem(java.lang.String customerID, java.lang.String itemID, int numberOfItem) {
        if (svrName.equals(SvrInfo.SVR_NAME_MONTREAL)) {
            return mport.returnNumOfItem(customerID, itemID, numberOfItem);
        } else if (svrName.equals(SvrInfo.SVR_NAME_TORONTO)) {
            return tport.returnNumOfItem(customerID, itemID, numberOfItem);
        }         
        return vport.returnNumOfItem(customerID, itemID, numberOfItem);
    }

}
