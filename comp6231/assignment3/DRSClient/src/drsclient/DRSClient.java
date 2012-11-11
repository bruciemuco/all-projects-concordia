/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package drsclient;

import java.io.PrintWriter;
import java.io.StringWriter;
import DRSCommon.*;

/**
 *
 * @author Ewan
 */
public class DRSClient {
    public static void main(String[] args) {
        // initialize SysLogger
        SysLogger.init();

        try {
                // simple test
                // one customer, one item, three stores
                String svrName = SvrInfo.SVR_NAME_MONTREAL;
                String customerID = "M10001";
                String itemID = DRSCommonService.TEST_ITEMID;
                int numberOfItem = 10;

                ConcurrentTest t = new ConcurrentTest(svrName, customerID, itemID, numberOfItem);
                t.simpleTest(svrName, customerID, itemID, numberOfItem);


                // concurrent test 
                // three users concurrently accessing one item
                itemID = DRSCommonService.TEST_ITEMID_CON;

                ConcurrentTest.concurrentTest(SvrInfo.SVR_NAME_MONTREAL, "M10001", itemID, numberOfItem);
                ConcurrentTest.concurrentTest(SvrInfo.SVR_NAME_TORONTO, "T10001", itemID, numberOfItem);
                ConcurrentTest.concurrentTest(SvrInfo.SVR_NAME_VANCOUVER, "V10001", itemID, numberOfItem);

                Thread.sleep(5*1000);
        } catch (Exception e) {
                StringWriter err = new StringWriter();
                e.printStackTrace(new PrintWriter(err));
                SysLogger.err(err.toString());
        }
    }


}
