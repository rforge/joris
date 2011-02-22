package org.rosuda.mapper.irexp;

import org.rosuda.irconnect.IREXP;
import org.rosuda.irconnect.IRMap;
import org.rosuda.mapper.AbstractGenericMapper;
import org.rosuda.mapper.MappedNodeTrace;
import org.rosuda.type.Node;

class MapMapper<T> extends AbstractGenericMapper<IREXP, T> {

	private final AbstractGenericMapper<IREXP, T> rootHandler;

	MapMapper(final AbstractGenericMapper<IREXP, T> rootHandler) {
		this.rootHandler = rootHandler;
	}

	protected void handleMap(final IREXP source, final Node.Builder<T> parent,
			final MappedNodeTrace<T> trace) {
		final IRMap map = source.asMap();
		for (final String key : map.keys()) {
			if (key == null)
				continue;// skip
			final Node.Builder<T> keyKey = parent.createChild(key);
			if (keyKey == null)
				continue;
			parent.add(keyKey);
			rootHandler.map(map.at(key), keyKey, trace);
		}
	}

}
