package org.rosuda.graph.service.search;

import org.rosuda.type.Value;
import org.rosuda.type.Value.Type;

public class BoolValueConstraint implements ValueConstraint<Boolean, BoolCompareType>{


	private final BoolCompareType operator;
	private final Boolean value;
	
	public BoolValueConstraint(final Boolean value, final BoolCompareType operator) {
		this.operator = operator;
		this.value = value;
	}
	@Override
	public Type getType() {
		return Value.Type.BOOL;
	}

	@Override
	public BoolCompareType getOperator() {
		return operator;
	}

	@Override
	public Boolean getValue() {
		return value;
	}

}
