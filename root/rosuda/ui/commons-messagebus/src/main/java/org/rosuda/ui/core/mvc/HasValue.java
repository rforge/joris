package org.rosuda.ui.core.mvc;

public interface HasValue<T> {

	public T getValue();
	
	public void setValue(final T value);
	
	public void addChangeListener(final ValueChangeListener<T> listener);
	
	public void removeChangeListener(final ValueChangeListener<T> listener);
	
	public interface ValueChangeListener<T> {
		public void onValueChange(final T newValue);
	}
}
