package org.rosuda.ui.event;

import java.util.Collections;
import java.util.HashSet;

import org.rosuda.graph.service.search.VertexConstraint;
import org.rosuda.ui.core.mvc.MessageBus;

public class ModelSearchEvent implements MessageBus.Event {

	private static final Iterable<VertexConstraint> EMPTY = Collections
			.unmodifiableCollection(new HashSet<VertexConstraint>());
	private final Iterable<VertexConstraint> constraints;

	public ModelSearchEvent(final Iterable<VertexConstraint> vertexConstraint) {
		this.constraints = (vertexConstraint == null) ? EMPTY
				: vertexConstraint;
	}

	public Iterable<VertexConstraint> getConstraints() {
		return constraints;
	}
}