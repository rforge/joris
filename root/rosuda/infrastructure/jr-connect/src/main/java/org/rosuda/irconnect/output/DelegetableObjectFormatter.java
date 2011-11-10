package org.rosuda.irconnect.output;

public abstract class DelegetableObjectFormatter<TYPE> extends AbstractObjectFormatter implements TypedObjectFormatter<TYPE>{

	protected final ObjectFormatter objectFormatter;
	
	public DelegetableObjectFormatter(final ObjectFormatter objectFormatter) {
		this.objectFormatter = objectFormatter;
	}
}
