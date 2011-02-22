package org.rosuda.visualizer.mvc.swing;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.rosuda.ui.core.mvc.HasValue;

public class JTextFieldHasValue implements HasValue<String> {

	private final JTextField field;
	private final List<HasValue.ValueChangeListener<String>> listeners = new ArrayList<HasValue.ValueChangeListener<String>>();
	
	public JTextFieldHasValue() {
		 this(new JTextField());
	}
	
	public JTextFieldHasValue(final JTextField field) {
		this.field = field;
		this.field.getDocument().addDocumentListener(new DocumentListener() {
			
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
	
	public String getValue() {
		return field.getText();
	}

	private void fireChangeEvent() {
		final String newValue = field.getText();
		for (final HasValue.ValueChangeListener<String> listener: listeners) {
			listener.onValueChange(newValue);
		}
	}
	
	public void setValue(final String value) {
		field.setText(value);
		fireChangeEvent();
	}

	public void addChangeListener(final HasValue.ValueChangeListener<String> listener) {
		listeners.add(listener);
	}

	public void removeChangeListener(final HasValue.ValueChangeListener<String> listener) {
		listeners.remove(listener);
	}
	
	public JTextField getJTextField() {
		return field;
	}

}
