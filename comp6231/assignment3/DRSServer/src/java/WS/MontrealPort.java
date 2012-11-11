/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package WS;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

/**
 * REST Web Service
 *
 * @author Ewan
 */
@Path("montrealport")
public class MontrealPort {
    private WS_client.Montreal port;

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of MontrealPort
     */
    public MontrealPort() {
        port = getPort();
    }

    /**
     * Invokes the SOAP method checkStock
     * @param itemID resource URI parameter
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("text/plain")
    @Consumes("text/plain")
    @Path("checkstock/")
    public String getCheckStock(@QueryParam("itemID") String itemID) {
        try {
            // Call Web Service Operation
            if (port != null) {
                java.lang.String result = port.checkStock(itemID);
                return result;
            }
        } catch (Exception ex) {
            // TODO handle custom exceptions here
        }
        return null;
    }

    /**
     * Invokes the SOAP method exchange
     * @param customerID resource URI parameter
     * @param boughtItemID resource URI parameter
     * @param boughtNumber resource URI parameter
     * @param desiredItemID resource URI parameter
     * @param desiredNumber resource URI parameter
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("text/plain")
    @Consumes("text/plain")
    @Path("exchange/")
    public String getExchange(@QueryParam("customerID") String customerID, @QueryParam("boughtItemID") String boughtItemID, @QueryParam("boughtNumber")
            @DefaultValue("0") int boughtNumber, @QueryParam("desiredItemID") String desiredItemID, @QueryParam("desiredNumber")
            @DefaultValue("0") int desiredNumber) {
        try {
            // Call Web Service Operation
            if (port != null) {
                int result = port.exchange(customerID, boughtItemID, boughtNumber, desiredItemID, desiredNumber);
                return new java.lang.Integer(result).toString();
            }
        } catch (Exception ex) {
            // TODO handle custom exceptions here
        }
        return null;
    }

    /**
     * Invokes the SOAP method returnNumOfItem
     * @param customerID resource URI parameter
     * @param itemID resource URI parameter
     * @param numberOfItem resource URI parameter
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("text/plain")
    @Consumes("text/plain")
    @Path("returnnumofitem/")
    public String getReturnNumOfItem(@QueryParam("customerID") String customerID, @QueryParam("itemID") String itemID, @QueryParam("numberOfItem")
            @DefaultValue("0") int numberOfItem) {
        try {
            // Call Web Service Operation
            if (port != null) {
                int result = port.returnNumOfItem(customerID, itemID, numberOfItem);
                return new java.lang.Integer(result).toString();
            }
        } catch (Exception ex) {
            // TODO handle custom exceptions here
        }
        return null;
    }

    /**
     * Invokes the SOAP method buy
     * @param customerID resource URI parameter
     * @param itemID resource URI parameter
     * @param numberOfItem resource URI parameter
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("text/plain")
    @Consumes("text/plain")
    @Path("buy/")
    public String getBuy(@QueryParam("customerID") String customerID, @QueryParam("itemID") String itemID, @QueryParam("numberOfItem")
            @DefaultValue("0") int numberOfItem) {
        try {
            // Call Web Service Operation
            if (port != null) {
                int result = port.buy(customerID, itemID, numberOfItem);
                return new java.lang.Integer(result).toString();
            }
        } catch (Exception ex) {
            // TODO handle custom exceptions here
        }
        return null;
    }

    /**
     *
     */
    private WS_client.Montreal getPort() {
        try {
            // Call Web Service Operation
            WS_client.Montreal_Service service = new WS_client.Montreal_Service();
            WS_client.Montreal p = service.getMontrealPort();
            return p;
        } catch (Exception ex) {
            // TODO handle custom exceptions here
        }
        return null;
    }
}
