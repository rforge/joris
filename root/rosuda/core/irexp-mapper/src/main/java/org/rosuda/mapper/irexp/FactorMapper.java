package org.rosuda.mapper.irexp;

import org.rosuda.irconnect.IREXP;
import org.rosuda.irconnect.IRFactor;
import org.rosuda.mapper.AbstractGenericMapper;
import org.rosuda.mapper.MappedNodeTrace;
import org.rosuda.type.Node;
import org.rosuda.type.Value;

class FactorMapper<T> extends AbstractGenericMapper<IREXP, T> {

	protected void handleMap(final IREXP source, final Node.Builder<T> parent, final MappedNodeTrace<T> trace) {
		final IRFactor factor = source.asFactor();
		if (factor.size() == 1) {
			final Node.Builder<T> holder = parent.createChild("rfactor");
			if (holder == null)
				return;
			parent.add(holder);
			holder.setValue(Value.newString(factor.at(0)));
		} else {
			final Node.Builder<T> holder = parent.createChild("rfactor");
			if (holder == null)
				return;
			parent.add(holder);
			for (int i=0; i<factor.size(); i++) {
				final Node.Builder<T> value = holder.createChild("factor");
				if (value == null)
					return;
				holder.add(value);
				value.setValue(Value.newString(factor.at(0)));
			}
		}
	}

}
