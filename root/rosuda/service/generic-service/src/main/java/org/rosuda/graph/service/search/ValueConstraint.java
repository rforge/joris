package org.rosuda.graph.service.search;

import org.rosuda.type.Value;

public interface ValueConstraint {
	
	boolean matches(final Value value);

}
