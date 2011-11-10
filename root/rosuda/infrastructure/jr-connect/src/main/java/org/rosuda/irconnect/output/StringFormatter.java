package org.rosuda.irconnect.output;

public class StringFormatter implements TypedObjectFormatter<String>{

	@Override
	public String format(final String value) {
		return value;
	}

}
