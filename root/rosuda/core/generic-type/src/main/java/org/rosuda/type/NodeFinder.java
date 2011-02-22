package org.rosuda.type;


public interface NodeFinder<T> {
	
	Node<T> findNode(final Node<T> root, final NodePath path);

	Node<T> findNode(final Node<T> root, final String nodePathAsString);

}
