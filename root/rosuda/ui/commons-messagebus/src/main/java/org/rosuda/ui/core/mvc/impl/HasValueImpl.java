package org.rosuda.ui.core.mvc.impl;

import java.util.ArrayList;
import java.util.List;

import org.rosuda.ui.core.mvc.HasValue;

public class HasValueImpl<T> implements HasValue<T> {

	private T value;
	private List<HasValue.ValueChangeListener<T>> listeners = new ArrayList<HasValue.ValueChangeListener<T>>();
	
	public HasValueImpl() {
		this(null);
	}
	
	public HasValueImpl(final T value) {
		this.value = value;
	}
	
	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
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
