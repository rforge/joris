package org.rosuda.mapper.irexp;

import java.util.logging.Logger;

import org.rosuda.irconnect.IREXP;
import org.rosuda.mapper.AbstractGenericMapper;
import org.rosuda.mapper.MappedNodeTrace;
import org.rosuda.type.Node;


class UnsupportedTypeMapper<T> extends AbstractGenericMapper<IREXP, T> {

	private static final Logger logger = Logger.getLogger(UnsupportedTypeMapper.class.getCanonicalName());
	private final String type;
	
	UnsupportedTypeMapper(final String type) {
		this.type = type;
	}
	

	protected void handleMap(final IREXP source, final Node.Builder<T> parent, final MappedNodeTrace<T> trace) {
		logger.warning("unhandeled Type \""+type+"\"");
	}

}
