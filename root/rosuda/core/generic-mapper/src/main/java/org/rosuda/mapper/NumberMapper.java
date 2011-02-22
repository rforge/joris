package org.rosuda.mapper;

import org.rosuda.type.Node;
import org.rosuda.type.Value;

public class NumberMapper<T> extends AbstractGenericMapper<Number,T> {

	protected void handleMap(final Number source, final Node.Builder<T> target, final MappedNodeTrace<T> trace) {
		final Node.Builder<T> nodeBuilder = target.createChild(source.getClass().getSimpleName());
		if (nodeBuilder == null)
			return;
		target.add(nodeBuilder);
		nodeBuilder.setValue(Value.newNumber(source));
	}

}
