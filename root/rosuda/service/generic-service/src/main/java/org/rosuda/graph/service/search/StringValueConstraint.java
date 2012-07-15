package org.rosuda.graph.service.search;

import org.rosuda.type.Value;
import org.rosuda.type.Value.Type;

public class StringValueConstraint implements ValueConstraint<String, StringCompareType>{

	private final StringCompareType operator;
	private final String value;
	
	public StringValueConstraint(final String string, final StringCompareType operator) {
		this.operator = operator;
		this.value = string;
	}
	
	@Override
	public Type getType() {
		return Value.Type.STRING;
	}

	@Override
	public StringCompareType getOperator() {
		return operator;
	}

	@Override
	public String getValue() {
		return value;
	}

}
