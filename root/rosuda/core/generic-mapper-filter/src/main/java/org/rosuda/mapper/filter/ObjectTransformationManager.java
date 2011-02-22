package org.rosuda.mapper.filter;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.rosuda.mapper.AbstractGenericMapper;
import org.rosuda.mapper.ObjectTransformationHandler;
import org.rosuda.type.Node;
import org.rosuda.type.NodeBuilderFactory;

public class ObjectTransformationManager<T> {

	private final NodeBuilderFactory<T> factory;
	private final ObjectTransformationHandler<T> handler;
	private final List<NodeFilter<T>> filters = new ArrayList<NodeFilter<T>>();
	private final List<NodeValueFilter<T>> valueFilters = new ArrayList<NodeValueFilter<T>>();
	
	
	public ObjectTransformationManager(final NodeBuilderFactory<T> factory) {
		this(factory, new ObjectTransformationHandler<T>());
	}
	
	public ObjectTransformationManager(final NodeBuilderFactory<T> factory, final ObjectTransformationHandler<T> handler) {
		this.handler = handler;
		this.factory = factory;
	}
		
	public void registerMapper(final Class<?> classObject, final AbstractGenericMapper<?, T> mapper) {
		if (this.handler == null)
			throw new IllegalStateException("can only register a mapper if handler is present");
		handler.registerMapper(classObject, mapper);
	}
	
	public Node<T> transform(final Object source) {
		if (this.factory == null)
			throw new IllegalStateException("factory has not been set.");
		final Node.Builder<T> root = new NodeFilterDelegate<T>(factory.createRoot(), filters, valueFilters);
		handler.transform(source, root);
		return root.build();
	}

	public void addFilter(final NodeFilter<T> filter) {
		filters.add(filter);
	}

	public void removeFilter(final NodeFilter<T> filter) {
		filters.remove(filter);
	}
	
	public final Iterable<NodeFilter<T>> getFilters() {
		return Collections.unmodifiableList(filters);
	}
	
	public void addFilter(final NodeValueFilter<T> filter) {
		valueFilters.add(filter);
	}

	public void removeFilter(final NodeValueFilter<T> filter) {
		valueFilters.remove(filter);
	}
	
	public final Iterable<NodeValueFilter<T>> getNodeValueFilters() {
		return Collections.unmodifiableList(valueFilters);
	}
	
}
