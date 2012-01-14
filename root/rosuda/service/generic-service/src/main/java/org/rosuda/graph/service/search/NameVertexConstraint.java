package org.rosuda.graph.service.search;

import org.rosuda.type.Node;

public class NameVertexConstraint extends AbstractVertexConstraint{

	private final String name;
	
	public NameVertexConstraint(final String name) {
		this.name = name;
	}
	
	@Override
	public boolean matches(final Node<?> vertex) {
		return name.equals(vertex.getName());
	}
	
	public String getName() {
		return name;
	}
	
}
