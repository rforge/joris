package org.rosuda.irconnect.output;

public class DoubleFormatter extends AbstractNumberFormatter<Double>{

	@Override
	public String format(final Double value) {
		if (Double.isInfinite(value)) {
			return getReplacement("Double.infinity");
		} else if (Double.isNaN(value)) {
			return getReplacement("Double.NaN");			
		} else {
			return super.format(value);
		}
	}

	@Override
	protected String handleFormat(final Double value) {
		return Double.toString(value);
	}

}
