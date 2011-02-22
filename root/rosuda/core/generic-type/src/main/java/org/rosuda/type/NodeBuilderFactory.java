package org.rosuda.type;

public interface NodeBuilderFactory<N> {

	public Node.Builder<N> createRoot();
	
}
