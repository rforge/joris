package org.rosuda.ui.core.mvc.impl;

import java.util.ArrayList;
import java.util.List;

import org.rosuda.ui.core.mvc.HasValue;

public abstract class AbstractHasValue<T> implements HasValue<T> {

    private List<HasValue.ValueChangeListener<T>> listeners = new ArrayList<HasValue.ValueChangeListener<T>>();

    protected void onValueChange(final T newValue) {

    }

    public final void setValue(T value) {
	if (valueChanged(value)) {
	    onValueChange(value);
	    fireChangeEvent(value);
	}
    }

    private final boolean valueChanged(final T newValue) {
	return newValue != getValue();
    }

    protected void fireChangeEvent(final T newValue) {
	for (final HasValue.ValueChangeListener<T> listener : new ArrayList<HasValue.ValueChangeListener<T>>(listeners)) {
	    listener.onValueChange(newValue);
	}
    }

    public final void addChangeListener(final HasValue.ValueChangeListener<T> listener) {
	this.listeners.add(listener);
    }

    public final void removeChangeListener(final HasValue.ValueChangeListener<T> listener) {
	this.listeners.remove(listener);
    }

}
