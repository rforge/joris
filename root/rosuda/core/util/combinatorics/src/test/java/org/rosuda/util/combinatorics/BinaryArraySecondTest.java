package org.rosuda.util.combinatorics;

import org.junit.Assert;
import org.junit.Test;


/**
 * @author araseg Created on 22.08.2004
 * 
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class BinaryArraySecondTest {

	public static final boolean DEBUG = false;

	@Test
	public void testFullPowerSets() {
		BinaryArray<Object> array = null;
		for (int i = 1; i < 10; i++) {
			array = new BinaryArrayFactory<Object>().create(i);
			final int size = (int) Math.pow(2, i);
			Assert.assertNotNull("array could not be instantiated");
			Assert.assertEquals(i + "-th number not " + size, size,
					countIterations(array, i));
		}
	}

	@Test
	public void testInteractions() {
		Assert.assertEquals(
				3,
				countIterations(new BinaryArrayFactory<Object>().create(2, 1),
						2, 1));
		Assert.assertEquals(
				4,
				countIterations(new BinaryArrayFactory<Object>().create(2, 2),
						2, 2));

		Assert.assertEquals(
				4,
				countIterations(new BinaryArrayFactory<Object>().create(3, 1),
						3, 1));
		Assert.assertEquals(
				7,
				countIterations(new BinaryArrayFactory<Object>().create(3, 2),
						3, 2));
		Assert.assertEquals(
				8,
				countIterations(new BinaryArrayFactory<Object>().create(3, 3),
						3, 3));

		Assert.assertEquals(
				5,
				countIterations(new BinaryArrayFactory<Object>().create(4, 1),
						4, 1));
		Assert.assertEquals(
				11,
				countIterations(new BinaryArrayFactory<Object>().create(4, 2),
						4, 2));
		Assert.assertEquals(
				15,
				countIterations(new BinaryArrayFactory<Object>().create(4, 3),
						4, 3));
		Assert.assertEquals(
				16,
				countIterations(new BinaryArrayFactory<Object>().create(4, 4),
						4, 4));

		Assert.assertEquals(
				14,
				countIterations(new BinaryArrayFactory<Object>().create(13, 1),
						13, 1));
		Assert.assertEquals(
				92,
				countIterations(new BinaryArrayFactory<Object>().create(13, 2),
						13, 2));
	}

	private int countIterations(final BinaryArray<?> array, final int... idx) {
		final StringBuilder prefix = new StringBuilder();
		if (idx != null) {
			prefix.append("{");
			for (int i = 0; i < idx.length; i++) {
				if (i > 0)
					prefix.append(", ");
				prefix.append(idx[i]);
			}
			prefix.append("}: ");

		}
		final String pre = prefix.toString();
		int count = 0;
		while (array.hasNext()) {
			if (DEBUG)
				System.out.print(pre);
			if (DEBUG)
				System.out.println(array);
			array.next();
			count++;
		}
		return count;
	}

}
