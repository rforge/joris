package org.rosuda.graph.service.search;

import org.rosuda.type.Value;
import org.rosuda.type.Value.Type;


 public class NumberConstraint implements ValueConstraint<Number, Relation>{

	private final Number number;
	private final Relation relation;
	
	public NumberConstraint(final Number number, final Relation relation) {
		if (number == null)
			throw new IllegalArgumentException("number must not be null");
		if (relation == null)
			throw new IllegalArgumentException("relation must not be null");
		this.number = number;
		this.relation = relation;
	}
	
	public Number eval() {
		return number;
	}
	
	public Relation getOperator() {
		return relation;
	}

	@Override
	public Type getType() {
		return Value.Type.NUMBER;
	}
}
