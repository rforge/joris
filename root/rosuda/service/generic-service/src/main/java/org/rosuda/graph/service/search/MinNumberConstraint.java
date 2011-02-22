package org.rosuda.graph.service.search;


public class MinNumberConstraint extends AbstractNumberConstraint{

	private final Number min;
	
	public MinNumberConstraint(final Number min) {
		this.min = min;
	}
	
	@Override
	boolean matches(final Number number) {
		return min.doubleValue() < number.doubleValue();
	}

	
}
