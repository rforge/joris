package org.rosuda.mapper.irexp;

import org.rosuda.irconnect.IRBool;
import org.rosuda.irconnect.IREXP;
import org.rosuda.mapper.AbstractGenericMapper;
import org.rosuda.mapper.MappedNodeTrace;
import org.rosuda.type.Node;
import org.rosuda.type.Value;

class BoolMapper<T> extends AbstractGenericMapper<IREXP, T> {

	protected void handleMap(final IREXP source, final Node.Builder<T> parent, final MappedNodeTrace<T> trace) {	
		final Node.Builder<T> boolContainer = parent.createChild("rbool");
		if (boolContainer == null)
			return;
		parent.add(boolContainer);
		boolContainer.setValue(Value.newString(convert(source.asBool())));
	}

	static String convert(final IRBool bool) {
		if (bool.isFALSE())
			return "false";
		else if (bool.isTRUE())
			return "true";
		return "n/a";
	}
}
