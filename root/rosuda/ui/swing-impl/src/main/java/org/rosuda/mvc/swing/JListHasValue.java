package org.rosuda.mvc.swing;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.JList;

import org.rosuda.ui.core.mvc.HasValue;
import org.rosuda.ui.core.mvc.impl.AbstractHasValue;

public class JListHasValue extends AbstractHasValue<List<String>> {

    private final List<String> elements = new ArrayList<String>();

    private class StringListModel extends AbstractListModel {

	/**
		 * 
		 */
	private static final long serialVersionUID = -5355106399227165442L;

	@Override
	public int getSize() {
	    return elements.size();
	}

	@Override
	public String getElementAt(final int index) {
	    return elements.get(index);
	}

    }

    private final JList field;
    private final List<HasValue.ValueChangeListener<List<String>>> listeners = new ArrayList<HasValue.ValueChangeListener<List<String>>>();

    public JListHasValue() {
	this(new JList());
    }

    public JListHasValue(final JList field) {
	this.field = field;
	field.setModel(new StringListModel());
	this.field.addVetoableChangeListener(new VetoableChangeListener() {

	    @Override
	    public void vetoableChange(final PropertyChangeEvent evt) throws PropertyVetoException {
		fireChangeEvent();
	    }
	});
    }

    public List<String> getValue() {
	return elements;
    }

    private void fireChangeEvent() {
	final List<String> newValue = Collections.unmodifiableList(getValue());
	for (final HasValue.ValueChangeListener<List<String>> listener : listeners) {
	    listener.onValueChange(newValue);
	}
    }

    @Override
    protected void onValueChange(List<String> newValue) {
	elements.clear();
	elements.addAll(newValue);
    }

    public void addValue(final String value) {
	this.elements.add(value);
    }

    public void removeValue(final String value) {
	this.elements.remove(value);
    }

    public void removeValueAt(final int index) {
	this.elements.remove(index);
    }

    public JList getJTextField() {
	return field;
    }

}
