package org.rosuda.graph.service.search;

import org.rosuda.type.Value;

public interface ValueConstraint<TYPE, OPERATOR> {

	Value.Type getType();
	OPERATOR getOperator();
	TYPE getValue();
}
