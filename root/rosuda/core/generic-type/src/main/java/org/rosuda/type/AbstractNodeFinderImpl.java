package org.rosuda.type;

public abstract class AbstractNodeFinderImpl<T> implements NodeFinder<T>{

    public final Node<T> findNode(final Node<T> parent, final String nodePathAsString) {
    	return findNode(parent, NodePath.Impl.parse(nodePathAsString));
    }

}