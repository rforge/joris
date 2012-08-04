package org.rosuda.ui.dialog;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.rosuda.irconnect.IREXP;
import org.rosuda.type.Node;
import org.rosuda.type.Value;

public class RootNodeWrapper implements Node<IREXP> {

    private final Node<IREXP> delegate;
    private final List<Node<IREXP>> uniqueChildren = new ArrayList<Node<IREXP>>();
    @SuppressWarnings("unchecked")
    private volatile SoftReference<Map<String, RootNodeWrapper>> uniqueChildMapping = new SoftReference(new TreeMap<String, RootNodeWrapper>());

    public RootNodeWrapper(final Node<IREXP> parent, final Iterable<Node<IREXP>> data) {
	this.delegate = parent;
	// skip "root" children (this is the model name)
	if (parent == null) {
	    buildUniqueChildren(new RootlessIterable<IREXP>(data));
	} else {
	    buildUniqueChildren(data);
	}
	uniqueChildMapping.get().clear();
    }

    private void buildUniqueChildren(final Iterable<Node<IREXP>> data) {
	for (Node<IREXP> child : data) {
	    if (child.getValue() == null) {
		final String uniqueName = child.getName();
		if (!uniqueChildMapping.get().containsKey(uniqueName)) {
		    final RootNodeWrapper wrapper = new RootNodeWrapper(child, child.getChildren());
		    uniqueChildren.add(wrapper);
		    uniqueChildMapping.get().put(uniqueName, wrapper);
		} else {
		    // den knoten gibt es schon, also nur kinder zufügen
		    final RootNodeWrapper existingChild = uniqueChildMapping.get().get(uniqueName);
		    existingChild.buildUniqueChildren(child.getChildren());
		}
	    }
	}
    }

    @Override
    public Node<IREXP> getParent() {
	return delegate;
    }

    @Override
    public Iterable<Node<IREXP>> getChildren() {
	return uniqueChildren;
    }

    @Override
    public Node<IREXP> childAt(int idx) {
	return uniqueChildren.get(idx);
    }

    @Override
    public int getChildCount() {
	return uniqueChildren.size();
    }

    @Override
    public Iterable<Node<IREXP>> getLinks() {
	return delegate.getLinks();
    }

    @Override
    public Node<IREXP> linkAt(int idx) {
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
