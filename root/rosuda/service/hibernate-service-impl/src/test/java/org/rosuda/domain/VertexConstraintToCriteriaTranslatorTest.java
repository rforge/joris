package org.rosuda.domain;

import static org.junit.Assert.assertNotNull;
import mockit.NonStrict;
import mockit.NonStrictExpectations;
import mockit.Verifications;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.junit.Before;
import org.junit.Test;
import org.rosuda.graph.service.VertexConstraintToCriteriaTranslator;
import org.rosuda.graph.service.search.NameVertexConstraint;

public class VertexConstraintToCriteriaTranslatorTest {

	private VertexConstraintToCriteriaTranslator translator;
	private @NonStrict Criteria criteria;
	private @NonStrict Criteria childCriteria;
	
	@Before
	public void setUp() {
		translator = new VertexConstraintToCriteriaTranslator();
	}
	
	@Test 
	public void knowsAvailableConstraintTypes() {
		new NonStrictExpectations() {
			{
				criteria.getAlias(); returns ("criteria");
				childCriteria.getAlias(); returns ("childCriteria");
				criteria.createCriteria(anyString, anyString); returns (childCriteria);
				childCriteria.add((Criterion)any); returns (criteria);
			}
		};
		assertNotNull(translator.toCriteria(criteria, new NameVertexConstraint(null) ,0));
		new Verifications() {
			{
				criteria.createCriteria("vertices", "v0"); times = 1;
				childCriteria.add((Criterion)any); times = 1;
			}
		};
	}
}
