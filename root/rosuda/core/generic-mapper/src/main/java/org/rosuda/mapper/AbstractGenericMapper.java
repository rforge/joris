package org.rosuda.mapper;

import org.rosuda.type.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractGenericMapper<SRC, TGT> {

    private static final Logger genericLogger = LoggerFactory.getLogger(AbstractGenericMapper.class.getCanonicalName());
    private final boolean isDelegating;

    /**
     * 
     * @param isDelegating
     *            will this mapper handle the class or just delegate to another
     *            mapper ?
     */
    protected AbstractGenericMapper(final boolean isDelegating) {
        this.isDelegating = isDelegating;
    }

    protected AbstractGenericMapper() {
        this(false);
    }

    public final void map(final SRC source, final Node.Builder<TGT> nodeBuilder, final MappedNodeTrace<TGT> trace) {
        if (!isDelegating && trace.willProduceLoop(source, nodeBuilder)) {
            genericLogger.warn("potential loop created for source " + source);
            nodeBuilder.createReference(trace.getLoopNode(source));
            // add generic backref node
            return;
        } else {
            handleMap(source, nodeBuilder, trace);
        }

    }

    protected abstract void handleMap(final SRC source, final Node.Builder<TGT> target, final MappedNodeTrace<TGT> trace);
}
