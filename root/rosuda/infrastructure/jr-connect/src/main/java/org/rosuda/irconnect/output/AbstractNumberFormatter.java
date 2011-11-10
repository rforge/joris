package org.rosuda.irconnect.output;

import java.text.NumberFormat;

public abstract class AbstractNumberFormatter<T extends Number> extends AbstractObjectFormatter implements TypedObjectFormatter<T> {

	@Override
	public String format(final T value) {
		if (value == null) {
			return getReplacement(null);
		}
		final NumberFormat numberFormat = getFormat(value.getClass());
		if (numberFormat != null)
			return numberFormat.format(value);
		return handleFormat(value);
	}
	
	protected abstract String handleFormat(T value);

}
