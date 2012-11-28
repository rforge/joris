package org.rosuda.mvc.swing;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

import org.rosuda.ui.core.mvc.HasValue;
import org.rosuda.ui.core.mvc.impl.AbstractHasValue;

public class JListHasValue<T> extends AbstractHasValue<List<T>> implements InvocationHandler, TypedDynamicListModel<T>{

    private final List<T> elements = new ArrayList<T>();

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
	public T getElementAt(final int index) {
	    return elements.get(index);
	}

	public void handleEvent(String methodName) {
	    if ("add".equals(methodName)) {
		fireIntervalAdded(this, elements.size() -1 , elements.size());
	    } else if ("remove".equals(methodName)) {
		fireIntervalRemoved(this, elements.size()  , elements.size() + 1);
	    }
	    
	}

    }

    private final JList field;
    private final List<HasValue.ValueChangeListener<List<T>>> listeners = new ArrayList<HasValue.ValueChangeListener<List<T>>>();
    private final ListModel delegateModel;

    public JListHasValue() {
	this(new JList());
    }

    public JListHasValue(final JList field) {
	this.field = field;
	this.delegateModel = new StringListModel();
	field.setModel(delegateModel);
	this.field.addVetoableChangeListener(new VetoableChangeListener() {

	    @Override
	    public void vetoableChange(final PropertyChangeEvent evt) throws PropertyVetoException {
		fireChangeEvent();
	    }
	});
    }

    @SuppressWarnings("unchecked")
    public List<T> getValue() {
	return (List<T>) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[] { List.class }, this);
    }

    private void fireChangeEvent() {
	final List<T> newValue = Collections.unmodifiableList(getValue());
	for (final HasValue.ValueChangeListener<List<T>> listener : listeners) {
	    listener.onValueChange(newValue);
	}
    }

    @Override
    protected void onValueChange(List<T> newValue) {
	elements.clear();
	elements.addAll(newValue);
    }
    
    

    public void addValue(final T value) {
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
    
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
	final Object result = method.invoke(elements, args);
	final String methodName = method.getName();
	if ("add".equals(methodName) || "remove".equals(methodName)) {
	    final ListModel listModel = this.field.getModel();
	    if (listModel instanceof JListHasValue.StringListModel) {
		final StringListModel eventListModel = (StringListModel) listModel;
		eventListModel.handleEvent(methodName);
	    }
	    this.fireChangeEvent();	
	}
	return result;
    }

    @Override
    public int getSize() {
	return delegateModel.getSize();
    }

    @Override
    public Object getElementAt(int index) {
	return delegateModel.getElementAt(index);
    }

    @Override
    public void addListDataListener(ListDataListener l) {
	delegateModel.addListDataListener(l);
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
	delegateModel.removeListDataListener(l);
    }

    @Override
    public void add(T value) {
	elements.add(value);
    }

    @Override
    public T remove(T value) {
	if (elements.remove(value)) {
	    return value;
	}
	return null;
    }

    @Override
    public T at(int index) {
        return elements.get(index);
    }
}
