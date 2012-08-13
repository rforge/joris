package org.rosuda.ui.core.mvc.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.WrapDynaBean;
import org.rosuda.ui.core.mvc.HasValue;

public class PropertyHasValueImpl<T> implements HasValue<T> {

    private final Field propertyDescriptor;
    private final Object propertyHolder;

    private List<HasValue.ValueChangeListener<T>> listeners = new ArrayList<HasValue.ValueChangeListener<T>>();

    public PropertyHasValueImpl(final Object object, final String fieldName) {
	try {
	    this.propertyDescriptor = object.getClass().getDeclaredField(fieldName);
	    this.propertyDescriptor.setAccessible(true);
	} catch (final Exception e) {
	    throw new RuntimeException(e);
	}
	this.propertyHolder = object;
    }

    @SuppressWarnings("unchecked")
    public T getValue() {
	try {
	    return (T) propertyDescriptor.get(propertyHolder);
	} catch (final Exception e) {
	    throw new RuntimeException(e);
	}
    }

    public void setValue(T value) {
	final boolean valueChanged = getValue() != value;
	try {
	    propertyDescriptor.set(propertyHolder, value);
	} catch (final Exception e) {
	    throw new RuntimeException(e);
	}
	if (valueChanged) {
	    fireChangeEvent(value);
	}
    }

    protected void fireChangeEvent(final T newValue) {
	for (final HasValue.ValueChangeListener<T> listener : listeners) {
	    listener.onValueChange(newValue);
	}
    }

    public void addChangeListener(final HasValue.ValueChangeListener<T> listener) {
	this.listeners.add(listener);
    }

    public void removeChangeListener(final HasValue.ValueChangeListener<T> listener) {
	this.listeners.remove(listener);
    }

}
