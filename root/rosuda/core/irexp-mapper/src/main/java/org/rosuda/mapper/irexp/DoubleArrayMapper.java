package org.rosuda.mapper.irexp;

import org.rosuda.irconnect.IREXP;
import org.rosuda.mapper.AbstractGenericMapper;
import org.rosuda.mapper.MappedNodeTrace;
import org.rosuda.type.Node;
import org.rosuda.type.Value;

class DoubleArrayMapper<T> extends AbstractGenericMapper<IREXP, T> {

	protected void handleMap(final IREXP source, final Node.Builder<T> parent, final MappedNodeTrace<T> trace) {
		final double[] values = source.asDoubleArray();
		if (values.length == 1) {
			parent.setValue(Value.newNumber(values[0]));
		} else {
			for (int i=0;i<values.length;i++) {
				final Node.Builder<T> container = parent.createChild(Double.class.getSimpleName());
				if (container == null)
					continue;
				parent.add(container);
				container.setValue(Value.newNumber(values[i]));
			}
		}	
	}

}
