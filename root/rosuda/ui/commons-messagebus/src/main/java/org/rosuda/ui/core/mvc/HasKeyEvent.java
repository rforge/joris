package org.rosuda.ui.core.mvc;

public interface HasKeyEvent<T> {

	public interface KeyEvent<T> {
		public enum Type {
			KEY_UP, KEY_DOWN, KEY_PRESSED
		}
		public java.awt.event.KeyEvent getEvent();
		public Type getType();
		public T getValue();
	}
	
	public void addKeyEventListener(final KeyListener<T> listener);
	
	public void removeKeyEventListener(final KeyListener<T> listener);
	
	public interface KeyListener<T> {
		public void onKeyEvent(final KeyEvent<T> event);
	}
}
