package org.rosuda.graph.service.search;

import org.junit.Assert;
import org.junit.Test;

/** 
 * this test case is mainly to check if a search condition can be formulated decently!
 * @author ralfseger
 *
 */
public class SearchByConstraintTest {
	
	@Test
	public void testSearch() {
		//search for 
		//a coefficient 
		//where the name is "dist"
		//     p-value<1e-10
		//and  extimate >0.15
		VertexConstraint distConstraint = new NameVertexConstraint("dist")
			.addChildConstraint(new NameVertexConstraint("estimate").addValueConstraint(new NumberConstraint(0, Relation.GT)).addValueConstraint(new NumberConstraint(0.15, Relation.LT)))
			.addChildConstraint(new NameVertexConstraint("p-value").addValueConstraint(new NumberConstraint(0, Relation.GT)).addValueConstraint(new NumberConstraint(1e-10, Relation.LT)))
		;
		Assert.assertNotNull(distConstraint);
		
	}
}
