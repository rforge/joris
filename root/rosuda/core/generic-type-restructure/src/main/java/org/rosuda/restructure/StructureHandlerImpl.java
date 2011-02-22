package org.rosuda.restructure;

import java.util.Iterator;

import org.rosuda.type.Node;
import org.rosuda.type.NodePath;
import org.rosuda.type.Node.Builder;
import org.rosuda.type.NodePath.Identifier;

public class StructureHandlerImpl<T> implements StructureHandler<T> {
	
	public Builder<T> findNode(final Builder<T> parent, final NodePath path) {
		if (parent == null)
			return null;
		if (path == null)
			return null;
		final Identifier id = path.getId();
		if (id.getName().equals(parent.getName()) && Node.ROOTNAME.equals(id.getName())&&id.getIndex()==0) {
			if (!path.hasNext() )
				return parent;
			else
				return findNode(parent, path.next());
		}
		int matchCount = -1;
		
		Builder<T> match = null;
		final Iterator<Builder<T>> iterator = parent.getChildren().iterator();
		while (iterator.hasNext() && matchCount < id.getIndex()) {
			match = iterator.next();
			if (match.getName().equals(id.getName())) {
				matchCount ++;
			}
		}
		if (matchCount == id.getIndex()) {
			if (path.hasNext()) {
				return findNode(match, path.next());
			} else {
				return match;
			}
			
		}
		return null;
	}

	public Builder<T> findNode(final Builder<T> parent, final String nodePathAsString) {
		return findNode(parent, NodePath.Impl.parse(nodePathAsString));
	}

	public Node.Builder<T> removeNode(final Node.Builder<T> root, final NodePath path) {
		final Node.Builder<T> node = findNode(root, path);
		if (node == null)
			return null;
		node.removeFromParent();
		return node;
	}

	public Node.Builder<T> removeNode(final Node.Builder<T> root, final String nodePathAsString) {
		return removeNode(root, NodePath.Impl.parse(nodePathAsString));
	}

	public Builder<T> moveNode(final Builder<T> root, final NodePath currentPath, final NodePath targetPath) {
		final Node.Builder<T> targetParent = findNode(root, targetPath);
		if (targetParent == null)
			return null;
		final Node.Builder<T> moveNode = removeNode(root, currentPath);
		if (moveNode == null)
			return null;
		targetParent.add(moveNode);
		return moveNode;
	}

	public Builder<T> moveNode(final Builder<T> root, final String currentPathAsString,
			final String targetPathAsString) {
		return moveNode(root, NodePath.Impl.parse(currentPathAsString), NodePath.Impl.parse(targetPathAsString));
	}

}
