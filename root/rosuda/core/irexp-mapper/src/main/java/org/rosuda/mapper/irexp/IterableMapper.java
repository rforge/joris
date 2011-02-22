package org.rosuda.mapper.irexp;

import org.rosuda.irconnect.IREXP;
import org.rosuda.mapper.AbstractGenericMapper;
import org.rosuda.mapper.MappedNodeTrace;
import org.rosuda.type.Node;


class IterableMapper<T> extends AbstractGenericMapper<IREXP, T> {

	private final AbstractGenericMapper<IREXP, T> rootHandler;
	
	IterableMapper(final AbstractGenericMapper<IREXP, T> rootHandler) {
		this.rootHandler = rootHandler;
	}
	
	protected void handleMap(final IREXP source, final Node.Builder<T> parent, final MappedNodeTrace<T> trace) {		
		for (final IREXP child:source.asIterable()) {
			rootHandler.map(child, parent, trace);
		}
	}

}
