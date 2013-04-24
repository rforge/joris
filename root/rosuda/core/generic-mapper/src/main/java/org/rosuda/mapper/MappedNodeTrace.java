package org.rosuda.mapper;

import java.util.IdentityHashMap;
import java.util.Map;

import org.rosuda.type.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MappedNodeTrace<NodeType> {

    private static final Logger logger = LoggerFactory.getLogger(MappedNodeTrace.class.getCanonicalName());
    private final Map<Object, Node.Builder<NodeType>> mappings = new IdentityHashMap<Object, Node.Builder<NodeType>>();

    MappedNodeTrace() {
    }

    public final boolean willProduceLoop(final Object object, final Node.Builder<NodeType> node) {
        if (mappings.containsKey(object)) {
            return true;
        } else {
            logger.debug("Object \"" + object + "\" is rejected by loop condition");
            mappings.put(object, node);
            return false;
        }

    }

    public Node.Builder<NodeType> getLoopNode(final Object key) {
        return mappings.get(key);
    }
}
