package org.rosuda.graph.service.search;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractVertexConstraint implements VertexConstraint {

	private final List<VertexConstraint> childConstraints = new ArrayList<VertexConstraint>();
	@SuppressWarnings("rawtypes")
	private final List<ValueConstraint> valueConstraints = new ArrayList<ValueConstraint>();
	
	@Override
	public VertexConstraint addChildConstraint(final VertexConstraint childConstraint) {
		childConstraints.add(childConstraint);
		return this;
	}

	@Override
	public VertexConstraint addValueConstraint(@SuppressWarnings("rawtypes") final ValueConstraint valueConstraint) {
		valueConstraints.add(valueConstraint);
		return this;
	}
	
	@Override
	public Iterable<VertexConstraint> getChildConstraints() {
		return childConstraints;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Iterable<ValueConstraint> getValueConstraints() {
		return valueConstraints;
	}
	
	

}
