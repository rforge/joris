package org.rosuda.graph.service.search;

import org.rosuda.type.Value;
import org.rosuda.type.Value.Type;


 public class NumberValueConstraint implements ValueConstraint<Number, Relation>{

	private final Number number;
	private final Relation relation;
	
	public NumberValueConstraint(final Number number, final Relation relation) {
		if (number == null)
			throw new IllegalArgumentException("number must not be null");
		if (relation == null)
			throw new IllegalArgumentException("relation must not be null");
		this.number = number;
		this.relation = relation;
	}
	
	public Number getValue() {
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
