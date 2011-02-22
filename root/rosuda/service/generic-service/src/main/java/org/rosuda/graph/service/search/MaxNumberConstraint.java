package org.rosuda.graph.service.search;

public class MaxNumberConstraint extends AbstractNumberConstraint {

	final Number max;
	
	public MaxNumberConstraint(final Number max) {
		this.max = max;
	}
	
	@Override
	boolean matches(Number number) {
		return max.doubleValue() > number.doubleValue();
	}

}
