package org.rosuda.mapper.irexp;

import org.rosuda.irconnect.IRBool;
import org.rosuda.irconnect.IREXP;
import org.rosuda.mapper.AbstractGenericMapper;
import org.rosuda.mapper.MappedNodeTrace;
import org.rosuda.type.Node;
import org.rosuda.type.Value;


class BoolArrayMapper<T> extends AbstractGenericMapper<IREXP, T> {

	protected void handleMap(final IREXP source, final Node.Builder<T> parent, final MappedNodeTrace<T> trace) {	
		final IRBool[] bool = source.asBoolArray();
		if (bool.length == 1) {
			final Node.Builder<T> boolContainer = parent.createChild("rbool");
			if (boolContainer == null)
				return;
			parent.add(boolContainer);
			boolContainer.setValue(Value.newString(BoolMapper.convert(bool[0])));
		} else {
			for (int i=0;i<bool.length;i++) {
				final Node.Builder<T> boolContainer = parent.createChild("rbool");
				if (boolContainer == null)
					continue;
				parent.add(boolContainer);
				boolContainer.setValue(Value.newString(BoolMapper.convert(bool[i])));
			}
		}
	}
	
}
