package org.rosuda.mapper.irexp;

import org.rosuda.irconnect.IREXP;
import org.rosuda.mapper.AbstractGenericMapper;
import org.rosuda.mapper.MappedNodeTrace;
import org.rosuda.type.Node;
import org.rosuda.type.Value;

class SymbolMapper<T> extends AbstractGenericMapper<IREXP, T> {

	protected void handleMap(final IREXP source, final Node.Builder<T> parent, final MappedNodeTrace<T> trace) {
		final Node.Builder<T> holder = parent.createChild("rsymbol");
		if (holder == null)
			return;
		parent.add(holder);
		final IREXP symbol = source.asSymbol();
		holder.setValue(Value.newString(symbol.asString()));
	}

}
