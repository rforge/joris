package org.rosuda.ui.core.mvc.impl;

public class HasValueImpl<T> extends AbstractHasValue<T> {

    private T value;

    public HasValueImpl() {
	this(null);
    }

    public HasValueImpl(final T value) {
	this.value = value;
    }

    public final T getValue() {
	return value;
    }

    @Override
    protected void onValueChange(T newValue) {
	value = newValue;
    }

}
