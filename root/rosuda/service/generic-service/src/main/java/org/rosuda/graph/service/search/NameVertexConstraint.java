package org.rosuda.graph.service.search;


public class NameVertexConstraint extends AbstractVertexConstraint{
//TODO add match mode strict
	private final String name;
	
	public NameVertexConstraint(final String name) {
		this.name = name;
	}
		
	public String getName() {
		return name;
	}
	
}
