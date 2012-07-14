package libs;

import static org.junit.Assert.*;

import org.junit.Test;

public class PrimeTest {

	@Test
	public void testIsPrime() {
		Prime p = Prime.INSTANCE;
		
		Result ret = p.isPrime(1);
		assertTrue(ret.msg.equals("false"));
		
		ret = p.isPrime(4);
		assertTrue(ret.msg.equals("false"));
		
		ret = p.isPrime(3);
		assertTrue(ret.msg.equals("true"));
		ret = p.isPrime(5);
		assertTrue(ret.msg.equals("true"));
	}

}
