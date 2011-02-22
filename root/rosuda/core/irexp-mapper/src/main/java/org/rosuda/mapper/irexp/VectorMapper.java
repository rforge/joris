package org.rosuda.mapper.irexp;

import org.rosuda.irconnect.IREXP;
import org.rosuda.irconnect.IRVector;
import org.rosuda.mapper.AbstractGenericMapper;
import org.rosuda.mapper.MappedNodeTrace;
import org.rosuda.type.Node;


class VectorMapper<T> extends AbstractGenericMapper<IREXP, T> {

	private final AbstractGenericMapper<IREXP, T> rootHandler;

	VectorMapper(final AbstractGenericMapper<IREXP, T> rootHandler) {
		this.rootHandler = rootHandler;
	}

	protected void handleMap(final IREXP source, final Node.Builder<T> parent, final MappedNodeTrace<T> trace) {		
		final IRVector vector = source.asVector();
		for (int i = 0; i < vector.size(); i++) {
			rootHandler.map(vector.at(i), parent, trace);
		}
	}


}
