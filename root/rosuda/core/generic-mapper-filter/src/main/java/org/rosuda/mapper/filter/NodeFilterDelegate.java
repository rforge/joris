package org.rosuda.mapper.filter;

import java.util.Collection;

import org.rosuda.type.Node;
import org.rosuda.type.Node.Builder;
import org.rosuda.type.Value;

class NodeFilterDelegate<T> implements Node.Builder<T>{

	private final Node.Builder<T> delegate;
	private final Collection<NodeFilter<T>> filters;
	private final Collection<NodeValueFilter<T>> valueFilters;

	NodeFilterDelegate(final Node.Builder<T> delegate, final Collection<NodeFilter<T>> filters, final Collection<NodeValueFilter<T>> valueFilters) {
		this.delegate = delegate;
		this.filters = filters;
		this.valueFilters = valueFilters;
	}
	
	public int getChildCount() {
		return delegate.getChildCount();
	}

	public Iterable<Node.Builder<T>> getChildren() {
		return delegate.getChildren();
	}

	public String getName() {
		return delegate.getName();
	}

	public Node.Builder<T> getParent() {
		return delegate.getParent();
	}

	public Value getValue() {
		return delegate.getValue();
	}

	public Node.Builder<T> childAt(final int idx) {
		return delegate.childAt(idx);
	}
	
	public Node.Builder<T> add(final Node.Builder<T> child) {
		delegate.add(unwrap(child));
		return delegate;
	}

	public Node.Builder<T> createChild(final String... nodeNames) {
		boolean isAllowed = true;
		for (final NodeFilter<T> filter: filters) {
			if (!filter.accept(delegate, nodeNames)) 
				isAllowed = false;
		}
		if (!isAllowed) {
			return null;
		}
		return new NodeFilterDelegate<T>(delegate.createChild(nodeNames), filters, valueFilters);
	}

	public void createReference(final Node.Builder<T> anotherNode) {
		delegate.createReference(unwrap(anotherNode));
	}


	public void delete(final Node.Builder<T> child) {
		delegate.delete(unwrap(child));
	}

	public void setValue(final Value value) {
		boolean isAllowed = true;
		for (final NodeValueFilter<T> filter: valueFilters) {
			if (!filter.accept(delegate, value)) 
				isAllowed = false;
		}
		if (!isAllowed)
			return;
		delegate.setValue(value);
	}
	
	@Override
	public String toString() {
		return delegate.toString();
	}

	public Node<T> build() {
		return delegate.build();
	}

	public Iterable<Builder<T>> getLinks() {
		return delegate.getLinks();
	}

	public Builder<T> linkAt(final int idx) {
		return delegate.linkAt(idx);
	}

	public int getLinkCount() {
		return delegate.getLinkCount();
	}

	public Builder<T> removeFromParent() {
		return wrap(delegate.removeFromParent());
	}
	
	private Builder<T> unwrap(final Builder<T> builder) {
		if (builder instanceof NodeFilterDelegate) {
			return ((NodeFilterDelegate<T>) builder).delegate;
		} else {
			return builder;
		}
	}
	
	private NodeFilterDelegate<T> wrap(final Builder<T> builder) {
		if (builder instanceof NodeFilterDelegate) {
			return (NodeFilterDelegate<T>) builder;
		} else {
			return new NodeFilterDelegate<T>(builder, filters, valueFilters);
		}
	}
}
