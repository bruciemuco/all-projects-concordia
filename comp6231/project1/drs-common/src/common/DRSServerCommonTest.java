package common;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class DRSServerCommonTest {
	private DRSServerCommon svr = new DRSServerCommon();
	String customerID = "M98866";
	String itemID = "8866";
	int numOfItem = 0;

	@Before
	public void setUp() throws Exception {
		svr.init("test", 3122);
		numOfItem = svr.checkLocalStock(itemID);
		if (numOfItem == 0) {
			itemID = "8855";
			numOfItem = svr.checkLocalStock(itemID);
			return;
		}
	}

	@Test
	public void testIfValidCustomerID() {
		assertEquals(true, svr.ifValidCustomerID("M12345"));
		assertEquals(true, svr.ifValidCustomerID("b99999"));
		assertEquals(false, svr.ifValidCustomerID("M9999a"));
		assertEquals(false, svr.ifValidCustomerID("199990"));
	}

	@Test
	public void testIfValidItemID() {
		assertEquals(true, svr.ifValidItemID("1234"));
		assertEquals(true, svr.ifValidItemID("9999"));
		assertEquals(false, svr.ifValidItemID("999a"));
		assertEquals(false, svr.ifValidItemID("1s90"));
	}

	@Test
	public void testBuy() {
		int ret = -1, numOfBought = 1;
		
		ret = svr.buy(customerID, itemID, numOfBought);
		assertEquals(0, ret);
		assertEquals(numOfItem - numOfBought, svr.checkLocalStock(itemID));

		numOfBought = numOfItem - numOfBought;
		ret = svr.buy(customerID, itemID, numOfBought);
		assertEquals(0, ret);
		assertEquals(0, svr.checkLocalStock(itemID));

		numOfBought = 1;
		ret = svr.buy(customerID, itemID, numOfBought);
		assertEquals(1, ret);
		assertEquals(0, svr.checkLocalStock(itemID));
	}

	@Test
	public void testReturnNumOfItem() {
		int ret = -1, numOfReturned = 10;
		numOfItem = svr.checkLocalStock(itemID);
		
		ret = svr.returnNumOfItem(customerID, itemID, numOfReturned);
		assertEquals(0, ret);
		assertEquals(numOfItem + numOfReturned, svr.checkLocalStock(itemID));

		ret = svr.buy(customerID, itemID, numOfReturned);
		assertEquals(0, ret);
		assertEquals(numOfItem, svr.checkLocalStock(itemID));

	}

}
