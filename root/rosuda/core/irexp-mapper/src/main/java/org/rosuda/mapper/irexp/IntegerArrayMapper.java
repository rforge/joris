package org.rosuda.mapper.irexp;

import org.rosuda.irconnect.IREXP;
import org.rosuda.mapper.AbstractGenericMapper;
import org.rosuda.mapper.MappedNodeTrace;
import org.rosuda.type.Node;
import org.rosuda.type.Value;

class IntegerArrayMapper<T> extends AbstractGenericMapper<IREXP, T> {

	protected void handleMap(final IREXP source, final Node.Builder<T> parent, final MappedNodeTrace<T> trace) {
		final int[] values = source.asIntArray();
		if (values.length == 1) {
			parent.setValue(Value.newNumber(values[0]));
		} else {
			for (int i=0;i<values.length;i++) {
				final Node.Builder<T> container = parent.createChild(Integer.class.getSimpleName());
				if (container==null)
					return;
				parent.add(container);
				container.setValue(Value.newNumber(values[i]));
			}
		}	
	}

}
