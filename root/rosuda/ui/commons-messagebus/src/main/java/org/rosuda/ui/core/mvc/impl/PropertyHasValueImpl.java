package org.rosuda.ui.core.mvc.impl;

import java.lang.reflect.Field;

public class PropertyHasValueImpl<T> extends AbstractHasValue<T> {

    private final Field propertyDescriptor;
    private final Object propertyHolder;

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

    @Override
    protected void onValueChange(T newValue) {
	try {
	    propertyDescriptor.set(propertyHolder, newValue);
	} catch (final Exception e) {
	    throw new RuntimeException(e);
	}
    }
}
