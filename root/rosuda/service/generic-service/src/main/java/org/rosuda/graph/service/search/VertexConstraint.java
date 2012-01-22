package org.rosuda.graph.service.search;


public interface VertexConstraint {

	VertexConstraint addChildConstraint(final VertexConstraint childConstraint);
	
	VertexConstraint addValueConstraint(@SuppressWarnings("rawtypes") final ValueConstraint valueConstraint);
	
	Iterable<VertexConstraint> getChildConstraints();
	
	@SuppressWarnings("rawtypes")
	Iterable<ValueConstraint> getValueConstraints();
}
