package org.rosuda.type;

import java.util.Iterator;

import org.rosuda.type.NodePath.Identifier;

public class NodeFinderImpl<T> extends AbstractNodeFinderImpl<T> implements NodeFinder<T> {

    public Node<T> findNode(final Node<T> parent, final NodePath path) {
	if (parent == null)
	    return null;
	if (path == null)
	    return null;
	final Identifier id = path.getId();
	if (id.getName().equals(parent.getName()) && Node.ROOTNAME.equals(id.getName()) && id.getIndex() == 0) {
	    if (!path.hasNext())
		return parent;
	    else
		return findNode(parent, path.next());
	}
	int matchCount = -1;

	Node<T> match = null;
	final Iterator<Node<T>> iterator = parent.getChildren().iterator();
	while (iterator.hasNext() && matchCount < id.getIndex()) {
	    match = iterator.next();
	    if (match.getName().equals(id.getName())) {
		matchCount++;
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

}
