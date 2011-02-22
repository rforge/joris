package org.rosuda.restructure;

import org.rosuda.type.Node;
import org.rosuda.type.NodePath;
import org.rosuda.type.Node.Builder;

public interface StructureHandler<T> {

	Node.Builder<T> findNode(final Node.Builder<T> root, final NodePath path);

	Node.Builder<T> findNode(final Builder<T> root, final String nodePathAsString);
	
	Node.Builder<T> removeNode(final Node.Builder<T> root, final NodePath path);
	
	Node.Builder<T> removeNode(final Node.Builder<T> root, final String nodePathAsString);
	
	Node.Builder<T> moveNode(final Node.Builder<T> root, final NodePath currentPath, final NodePath targetPath);

	Node.Builder<T> moveNode(final Node.Builder<T> root, final String currentPathAsString, final String targetPathAsString);

}
