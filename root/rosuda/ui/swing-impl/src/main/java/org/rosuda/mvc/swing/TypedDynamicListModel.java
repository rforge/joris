package org.rosuda.mvc.swing;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;

public interface TypedDynamicListModel<T> extends ListModel {

    void add(T value);
    
    T remove(T value);
    
    T at(int index);
    
    public static class Impl<T> extends DefaultListModel implements TypedDynamicListModel<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8351885509244429742L;

	@Override
	public void add(T value) {
	    super.addElement(value);
	}

	@Override
	public T remove(T value) {
	    if (super.removeElement(value)) {
		return value;
	    }
	    return null;
	}

	@Override
	public T at(int index) {
	    return (T) super.get(index);
	}
    }
}
