package org.rosuda.graph.service;

import java.util.List;

import org.rosuda.graph.service.search.VertexConstraint;
import org.rosuda.type.Node;

public interface GraphService<T> {

	public Long store(final Node<T> graph);
	public void delete(final Node<T> graph);
	public Node<T> read(final Long id);
	
	public List<Node<T>> find(final Iterable<VertexConstraint> vertexConstraint);
	
	public List<Node<T>> list();
	
}
