package org.rosuda.type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostfixNodeFinderImpl<T> extends AbstractNodeFinderImpl<T> {

    private static final Logger LOG = LoggerFactory.getLogger(PostfixNodeFinderImpl.class);
    
    protected List<String> revert(final NodePath path) {
	if (path == null)
	    return null;
	final List<String> ids = new ArrayList<String>();
	NodePath walker = path;
	while (walker.hasNext()) {
	    ids.add(walker.getId().getName());
	    walker = walker.next();
	}
	ids.add(walker.getId().getName());
	Collections.reverse(ids);
	return Collections.unmodifiableList(ids);
    }
    
    @Override
    public Node<T> findNode(Node<T> parent, NodePath path) {
	if (parent == null || path == null)
	    return null;
	final List<String> ids = revert(path);
	final List<Node<T>> matches = new ArrayList<Node<T>>();
	return findMatchingNode(parent, path, ids, matches);
    }
    private Node<T> findMatchingNode(Node<T> parent, NodePath path, List<String> ids, List<Node<T>> matches) {
	if (ids.get(0).equals(parent.getName())) {
	    final Node<T> match = matchParents(parent, ids);
	    if (match != null) {
		matches.add(match);
	    }
	}
	final Node<T> uniqueMatch = findUniqueMatch(parent, path, matches);
	if (uniqueMatch != null) {
	    return uniqueMatch;
	}
	for (final Node<T> child : parent.getChildren()) {
	    final Node<T> match = findMatchingNode(child, path, ids, matches);
	    if (match != null)
		return match;
	}
	return null;
    }

    private Node<T> findUniqueMatch(Node<T> parent, NodePath path, List<Node<T>> matches) {
	if (!matches.isEmpty()) {
	    if (matches.size() == 1) {
		return matches.get(0);
	    } else {
		LOG.warn("non-unique match : "+path.toString()+" in "+parent);
		return null;
	    }
	}
	return null;
    }

    private Node<T> matchParents(final Node<T> parent, final List<String> ids) {
	int matches = 0;
	Node<T> walker = parent;
	int idx = 0;
	while (walker != null && idx < ids.size()) {
	    if (walker.getName().equals(ids.get(idx))) {
		matches ++;
	    }
	    idx ++;
	    walker = walker.getParent();
	}
	if (matches == ids.size())
	    return parent;
	return null;
    }
    
}
