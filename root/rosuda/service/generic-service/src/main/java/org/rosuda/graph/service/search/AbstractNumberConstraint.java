package org.rosuda.graph.service.search;

import org.rosuda.type.Value;

 abstract class AbstractNumberConstraint implements ValueConstraint{

	@Override
	public final boolean matches(final Value value) {
		final Number number = value.getNumber();
		if (number == null)
			return false;
		return matches(number);
	}

	abstract boolean matches(final Number number);
}
