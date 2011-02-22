package org.rosuda.visualizer.mvc.swing;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;

import org.rosuda.ui.core.mvc.HasValue;

public class JLabelHasValue implements HasValue<String> {

	private final JLabel label;
	private final List<HasValue.ValueChangeListener<String>> listeners = new ArrayList<HasValue.ValueChangeListener<String>>();
	
	public JLabelHasValue() {
		this(new JLabel());
	}
	
	public JLabelHasValue(final JLabel label) {
		this.label = label;
	}
	
	public String getValue() {
		return label.getText();
	}

	public void setValue(final String value) {
		label.setText(value);
	}

	public void addChangeListener(final HasValue.ValueChangeListener<String> listener) {
		listeners.add(listener);
	}

	public void removeChangeListener(final HasValue.ValueChangeListener<String> listener) {
		listeners.remove(listener);
	}
	
	public JLabel getJLabel() {
		return label;
	}

}
