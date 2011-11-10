package org.rosuda.irconnect.output;

public interface TypedObjectFormatter<TYPE> {

	public String format(final TYPE value);
}
