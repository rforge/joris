package org.rosuda.mvc.swing;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import org.rosuda.ui.core.mvc.HasValue;

public class DocumentHasValue<T> implements HasValue<T> {

	private final Document document;
	private final DocumentValueAdapter<T> adapter;
	private final List<HasValue.ValueChangeListener<T>> listeners = new ArrayList<HasValue.ValueChangeListener<T>>();
		
	public DocumentHasValue(final Document document, final DocumentValueAdapter<T> adapter) {
		this.document = document;
		this.adapter = adapter;
		this.document.addDocumentListener(new DocumentListener() {
			
			public void removeUpdate(final DocumentEvent de) {
				fireChangeEvent();
			}
			
			public void insertUpdate(final DocumentEvent de) {
				fireChangeEvent();
			}
			
			public void changedUpdate(final DocumentEvent de) {
				fireChangeEvent();
			}
		});
	}
	
	public T getValue() {
		return adapter.getValue();
	}

	private void fireChangeEvent() {
		final T newValue = adapter.getValue();
		for (final HasValue.ValueChangeListener<T> listener: listeners) {
			listener.onValueChange(newValue);
		}
	}
	
	public void setValue(final T value) {
		adapter.setValue(value);
		fireChangeEvent();
	}

	public void addChangeListener(final HasValue.ValueChangeListener<T> listener) {
		listeners.add(listener);
	}

	public void removeChangeListener(final HasValue.ValueChangeListener<T> listener) {
		listeners.remove(listener);
	}
}
