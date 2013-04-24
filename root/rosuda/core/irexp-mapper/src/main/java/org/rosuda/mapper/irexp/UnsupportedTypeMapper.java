package org.rosuda.mapper.irexp;

import org.rosuda.irconnect.IREXP;
import org.rosuda.mapper.AbstractGenericMapper;
import org.rosuda.mapper.MappedNodeTrace;
import org.rosuda.type.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class UnsupportedTypeMapper<T> extends AbstractGenericMapper<IREXP, T> {

    private static final Logger logger = LoggerFactory.getLogger(UnsupportedTypeMapper.class.getCanonicalName());
    private final String type;

    UnsupportedTypeMapper(final String type) {
        this.type = type;
    }

    protected void handleMap(final IREXP source, final Node.Builder<T> parent, final MappedNodeTrace<T> trace) {
        logger.warn("unhandeled Type \"" + type + "\"");
    }

}
