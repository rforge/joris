package org.rosuda.ui.dialog;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.rosuda.type.Node;
import org.rosuda.type.Value;

public class RootNodeWrapper<T> implements Node<T> {

    private final Node<T> delegate;
    private final List<Node<T>> uniqueChildren = new ArrayList<Node<T>>();
    private volatile SoftReference<Map<String, RootNodeWrapper<T>>> uniqueChildMapping = new SoftReference<Map<String, RootNodeWrapper<T>>>(new TreeMap<String, RootNodeWrapper<T>>());

    public RootNodeWrapper(final Node<T> parent, final Iterable<Node<T>> data) {
	this.delegate = parent;
	// skip "root" children (this is the model name)
	if (parent == null) {
	    buildUniqueChildren(new RootlessIterable<T>(data));
	} else {
	    buildUniqueChildren(data);
	}
	uniqueChildMapping.get().clear();
    }

    private void buildUniqueChildren(final Iterable<Node<T>> data) {
	for (Node<T> child : data) {
	    if (child.getValue() == null) {
		final String uniqueName = child.getName();
		if (!uniqueChildMapping.get().containsKey(uniqueName)) {
		    final RootNodeWrapper<T> wrapper = new RootNodeWrapper<T>(child, child.getChildren());
		    uniqueChildren.add(wrapper);
		    uniqueChildMapping.get().put(uniqueName, wrapper);
		} else {
		    // den knoten gibt es schon, also nur kinder zufügen
		    final RootNodeWrapper<T> existingChild = uniqueChildMapping.get().get(uniqueName);
		    existingChild.buildUniqueChildren(child.getChildren());
		}
	    }
	}
    }

    @Override
    public Node<T> getParent() {
	return delegate;
    }

    @Override
    public Iterable<Node<T>> getChildren() {
	return uniqueChildren;
    }

    @Override
    public Node<T> childAt(int idx) {
	return uniqueChildren.get(idx);
    }

    @Override
    public int getChildCount() {
	return uniqueChildren.size();
    }

    @Override
    public Iterable<Node<T>> getLinks() {
	return delegate.getLinks();
    }

    @Override
    public Node<T> linkAt(int idx) {
	return delegate.linkAt(idx);
    }

    @Override
    public int getLinkCount() {
	return delegate.getLinkCount();
    }

    @Override
    public String getName() {
	if (delegate == null)
	    return "";
	return delegate.getName();
    }

    @Override
    public Value getValue() {
	if (delegate == null) {
	    return null;
	}
	return delegate.getValue();
    }

}
