package org.rosuda.mapper;

import java.util.logging.Logger;

import org.rosuda.type.Node;

public abstract class AbstractGenericMapper<SRC,TGT>{

	private static final Logger genericLogger = Logger.getLogger(AbstractGenericMapper.class.getCanonicalName());
	private final boolean isDelegating;
	
	/**
	 * 
	 * @param isDelegating will this mapper handle the class or just delegate to another mapper ?
	 */
	protected AbstractGenericMapper(final boolean isDelegating) {
		this.isDelegating = isDelegating;
	}
	
	protected AbstractGenericMapper() {
		this(false);
	}
	
	public final void map(final SRC source, final Node.Builder<TGT> nodeBuilder, final MappedNodeTrace<TGT> trace) {
		if (!isDelegating && trace.willProduceLoop(source, nodeBuilder)) {
			genericLogger.warning("potential loop created for source "+source);
			nodeBuilder.createReference(trace.getLoopNode(source));			
			//add generic backref node
			return;
		} else {
			handleMap(source, nodeBuilder, trace);
		}
		
	}
	
	protected abstract void handleMap(final SRC source, final Node.Builder<TGT> target, final MappedNodeTrace<TGT> trace);
}
