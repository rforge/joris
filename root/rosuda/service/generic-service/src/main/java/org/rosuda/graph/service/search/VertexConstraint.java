package org.rosuda.graph.service.search;

import org.rosuda.type.Node;

public interface VertexConstraint {

	boolean matches(final Node<?> vertex);
	
	VertexConstraint addChildConstraint(final VertexConstraint childConstraint);
	
	VertexConstraint addValueConstraint(final ValueConstraint valueConstraint);
	
	Iterable<VertexConstraint> getChildConstraints();
	
	Iterable<ValueConstraint> getValueConstraints();
}
