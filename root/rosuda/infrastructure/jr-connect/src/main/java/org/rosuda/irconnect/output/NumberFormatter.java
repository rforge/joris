package org.rosuda.irconnect.output;

public class NumberFormatter extends AbstractNumberFormatter<Number> {

	@Override
	protected String handleFormat(Number value) {
		return value.toString();
	}

}
