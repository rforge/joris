package org.rosuda.type.impl;

import org.rosuda.type.Node.Builder;

public class NodeBuilderFactory<T> implements org.rosuda.type.NodeBuilderFactory<T>{

	public NodeBuilderFactory() {	
	}
	
	public Builder<T> createRoot() {
		final Graph<T> graph = new Graph<T>();
		return graph.createRoot();
	}

}
