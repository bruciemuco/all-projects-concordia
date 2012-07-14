package libs;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class MySqrtTest {
	@SuppressWarnings("deprecation")
	@Test
	public void testGetRoot() {
		MySqrt mysqrt = MySqrt.INSTANCE;
		System.out.print("-: " + mysqrt.getRoot(0));
		assertTrue(0.0 == mysqrt.getRoot(0));
		assertTrue(0.31622776601683794 == mysqrt.getRoot(0.1));
		assertTrue(1.000000000000001 == mysqrt.getRoot(1));
		assertTrue(1.2 == mysqrt.getRoot(1.44));
		assertTrue(1.7320508075688772 == mysqrt.getRoot(3));
		assertTrue(2.0 == mysqrt.getRoot(4));
		assertTrue(3.0 == mysqrt.getRoot(9));

	}

}
