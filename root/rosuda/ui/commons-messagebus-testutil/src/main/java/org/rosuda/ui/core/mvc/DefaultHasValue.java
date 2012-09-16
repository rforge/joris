package org.rosuda.ui.core.mvc;

import org.rosuda.ui.core.mvc.impl.AbstractHasValue;

public class DefaultHasValue<T> extends AbstractHasValue<T> {

    private T value;

    @Override
    public final T getValue() {
	return value;
    }

    protected final void onValueChange(T newValue) {
	this.value = newValue;
    };
}
