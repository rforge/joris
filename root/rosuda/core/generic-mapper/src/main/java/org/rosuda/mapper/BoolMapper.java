package org.rosuda.mapper;

import org.rosuda.type.Node;
import org.rosuda.type.Value;

public class BoolMapper<T> extends AbstractGenericMapper<Boolean,T> {

	@Override
	protected void handleMap(final Boolean source, final Node.Builder<T> target, final MappedNodeTrace<T> trace) {
		final Node.Builder<T> nodeBuilder = target.createChild(Boolean.class.getSimpleName());
		if (nodeBuilder == null)
			return;
		target.add(nodeBuilder);
		nodeBuilder.setValue(Value.newBool(source));
	}
}
