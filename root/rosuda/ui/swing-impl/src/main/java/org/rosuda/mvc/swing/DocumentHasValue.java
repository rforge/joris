package org.rosuda.mvc.swing;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import org.rosuda.ui.core.mvc.impl.AbstractHasValue;

public class DocumentHasValue<T> extends AbstractHasValue<T> {

    private final Document document;
    private final DocumentValueAdapter<T> adapter;

    public DocumentHasValue(final Document document, final DocumentValueAdapter<T> adapter) {
	this.document = document;
	this.adapter = adapter;
	this.document.addDocumentListener(new DocumentListener() {

	    public void removeUpdate(final DocumentEvent de) {
		fireChangeEvent(adapter.getValue());
	    }

	    public void insertUpdate(final DocumentEvent de) {
		fireChangeEvent(adapter.getValue());
	    }

	    public void changedUpdate(final DocumentEvent de) {
		fireChangeEvent(adapter.getValue());
	    }
	});
    }

    public T getValue() {
	return adapter.getValue();
    }

    @Override
    protected void onValueChange(T newValue) {
	adapter.setValue(newValue);
    }

}
