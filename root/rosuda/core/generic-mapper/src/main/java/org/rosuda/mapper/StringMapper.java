package org.rosuda.mapper;

import org.rosuda.type.Node;
import org.rosuda.type.Value;

public class StringMapper<T> extends AbstractGenericMapper<String,T> {

	protected void handleMap(final String source, final Node.Builder<T> target, final MappedNodeTrace<T> trace) {
		final Node.Builder<T> container = target.createChild(String.class.getSimpleName());
		if (container == null)
			return;
		target.add(container);
		container.setValue(Value.newString(source));
	}

}
