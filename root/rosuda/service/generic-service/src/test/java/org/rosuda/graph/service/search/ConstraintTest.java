package org.rosuda.graph.service.search;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.rosuda.type.Value;

public class ConstraintTest{

	@SuppressWarnings("rawtypes")
	private ValueConstraintEvaluator evaluator;
	
	@Before
	public void setUp() {
		evaluator = new ValueConstraintEvaluator.Impl();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testNumberConstraints() {
		final Value int42 = Value.newNumber(42);
		assertTrue(evaluator.matches(new NumberValueConstraint(1, Relation.GT), int42));
		assertTrue(evaluator.matches(new NumberValueConstraint(1, Relation.GE), int42));
		assertTrue(evaluator.matches(new NumberValueConstraint(42, Relation.EQ), int42));
		assertTrue(evaluator.matches(new NumberValueConstraint(100, Relation.LE), int42));
		assertTrue(evaluator.matches(new NumberValueConstraint(100, Relation.LT), int42));
		assertFalse(evaluator.matches(new NumberValueConstraint(1, Relation.LT), int42));
		assertFalse(evaluator.matches(new NumberValueConstraint(1, Relation.LE), int42));
		assertFalse(evaluator.matches(new NumberValueConstraint(1, Relation.EQ), int42));
		assertFalse(evaluator.matches(new NumberValueConstraint(100, Relation.GE), int42));
		assertFalse(evaluator.matches(new NumberValueConstraint(100, Relation.GT), int42));
	}
}
