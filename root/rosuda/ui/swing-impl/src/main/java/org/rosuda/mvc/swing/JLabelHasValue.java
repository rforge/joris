package org.rosuda.mvc.swing;

import javax.swing.JLabel;

import org.rosuda.ui.core.mvc.impl.AbstractHasValue;

public class JLabelHasValue extends AbstractHasValue<String> {

    private final JLabel label;

    public JLabelHasValue() {
	this(new JLabel());
    }

    public JLabelHasValue(final JLabel label) {
	this.label = label;
    }

    public String getValue() {
	return label.getText();
    }

    @Override
    protected void onValueChange(String newValue) {
	label.setText(newValue);
    }

    public JLabel getJLabel() {
	return label;
    }

}
