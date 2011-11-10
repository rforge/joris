package org.rosuda.rengine;

import org.rosuda.REngine.REXPLogical;
import org.rosuda.irconnect.IRBool;


public class REngineRBool implements IRBool {

	private final REXPLogical delegate;
	private final int index;
	REngineRBool(final REXPLogical delegate) {
		this(delegate,0);
	}
	
	REngineRBool(final REXPLogical delegate, final int i) {
		if (delegate == null)
			throw new IllegalArgumentException("missing required delegate.");
		this.delegate = delegate;
		this.index = i;

	}
	public boolean isFALSE() {
		return sanityCheck() && delegate.isFALSE()[index];
	}

	public boolean isNA() {
		return sanityCheck() && delegate.isNA()[index];
	}

	public boolean isTRUE() {
		return sanityCheck() && delegate.isTRUE()[index];
	}

	private boolean sanityCheck() {
		final int length = delegate.length();
		return (length > index);
	}
}
