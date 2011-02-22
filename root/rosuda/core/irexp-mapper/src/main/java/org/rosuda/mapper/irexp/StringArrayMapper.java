package org.rosuda.mapper.irexp;

import org.rosuda.irconnect.IREXP;
import org.rosuda.mapper.AbstractGenericMapper;
import org.rosuda.mapper.MappedNodeTrace;
import org.rosuda.type.Node;
import org.rosuda.type.Value;

class StringArrayMapper<T> extends AbstractGenericMapper<IREXP,T> {

	protected void handleMap(final IREXP source, final Node.Builder<T> parent, final MappedNodeTrace<T> trace) {		final String[] values = source.asStringArray();
		if (values.length == 1) {
			parent.setValue(Value.newString(values[0]));
		} else {
			for (int i=0;i<values.length;i++) {
				final Node.Builder<T> container = parent.createChild(String.class.getSimpleName());
				if (container == null)
					return;
				parent.add(container);
				container.setValue(Value.newString(values[i]));
			}
		}
	}

}
