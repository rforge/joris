package org.rosuda.irconnect.output;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

public class ObjectFormatterTest {

	private ObjectFormatter objectFormatter;
	
	@Before
	public void setUp() {
		Locale.setDefault(Locale.US);
		objectFormatter = new ObjectFormatter();
	}

	@Test
	public void testFormatNull() {
		assertEquals("null", objectFormatter.format(null));
	}

	@Test
	public void testFormatString() {
		assertEquals("String", objectFormatter.format("String"));
	}
	
	@Test
	public void testFormatStringArray() {
		assertEquals("[String, 1, 2, 3]", objectFormatter.format(new String[]{"String","1","2","3"}));
	}
	
	@Test
	public void testFormatStringCollection() {
		assertEquals("[String, 1, 2, 3]", objectFormatter.format(Arrays.asList(new String[]{"String","1","2","3"})));
	}

	@Test
	public void testFormatDouble() {
		assertEquals("1.00", objectFormatter.format(1.00));
		assertEquals("Not Numeric", objectFormatter.format(Double.NaN));
	}
	
	@Test
	public void testFormatInteger() {
		assertEquals("1", objectFormatter.format(1));
	}

	@Test
	public void testFormatNumber() {
		assertEquals("1", objectFormatter.format(new BigInteger("1")));
		assertEquals("1.00", objectFormatter.format(new BigDecimal(1.00)));
	}
	
}
