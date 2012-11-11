/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package WS;

import DRSCommon.DRSServerCommon;
import DRSCommon.SvrInfo;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;

/**
 *
 * @author Ewan
 */
@WebService(serviceName = "Toronto")
@Stateless()
public class Toronto {
    private DRSServerCommon svr = new DRSServerCommon(SvrInfo.SVR_NAME_TORONTO);
        
    /**
     * Web service operation
     */
    @WebMethod(operationName = "buy")
    public int buy(@WebParam(name = "customerID") String customerID, @WebParam(name = "itemID") String itemID, @WebParam(name = "numberOfItem") int numberOfItem) {
        return svr.buy(customerID, itemID, numberOfItem);
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "returnNumOfItem")
    public int returnNumOfItem(@WebParam(name = "customerID") String customerID, @WebParam(name = "itemID") String itemID, @WebParam(name = "numberOfItem") int numberOfItem) {
        return svr.returnNumOfItem(customerID, itemID, numberOfItem);
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "checkStock")
    public String checkStock(@WebParam(name = "itemID") String itemID) {
        return svr.checkStock(itemID);
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "exchange")
    public int exchange(@WebParam(name = "customerID") String customerID, @WebParam(name = "boughtItemID") String boughtItemID, @WebParam(name = "boughtNumber") int boughtNumber, @WebParam(name = "desiredItemID") String desiredItemID, @WebParam(name = "desiredNumber") int desiredNumber) {
        return svr.exchange(customerID, boughtItemID, boughtNumber, desiredItemID, desiredNumber);
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "chkStockFromSvr")
    public String chkStockFromSvr(@WebParam(name = "itemID") String itemID) {
               return svr.chkStockFromSvr(itemID);

    }
}
