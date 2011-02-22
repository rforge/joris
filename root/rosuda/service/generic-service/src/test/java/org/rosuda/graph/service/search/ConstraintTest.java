package org.rosuda.graph.service.search;


import org.junit.Assert;
import org.junit.Test;
import org.rosuda.type.Value;

public class ConstraintTest{

	@Test
	public void testNumberConstraints() {
		final Value int42 = Value.newNumber(42);
		Assert.assertTrue(new MinNumberConstraint(1).matches(int42));
		Assert.assertTrue(new MaxNumberConstraint(100).matches(int42));
	}
}
