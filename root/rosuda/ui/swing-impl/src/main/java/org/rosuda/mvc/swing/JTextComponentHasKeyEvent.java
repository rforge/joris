package org.rosuda.mvc.swing;

import java.util.ArrayList;
import java.util.List;

import javax.swing.text.JTextComponent;

import org.rosuda.ui.core.mvc.HasKeyEvent;

public class JTextComponentHasKeyEvent<T> implements HasKeyEvent<T>{

	private final DocumentValueAdapter<T> adapter;
	private final List<HasKeyEvent.KeyListener<T>> listeners = new ArrayList<HasKeyEvent.KeyListener<T>>();
	
	public JTextComponentHasKeyEvent(final JTextComponent textComponent, final DocumentValueAdapter<T> adapter) {
		this.adapter = adapter;
		textComponent.addKeyListener(new java.awt.event.KeyListener() {
			@Override
			public void keyTyped(final java.awt.event.KeyEvent e) {
				fireEvent(KeyEvent.Type.KEY_DOWN, e);
			}
			@Override
			public void keyReleased(final java.awt.event.KeyEvent e) {
				fireEvent(KeyEvent.Type.KEY_UP, e);
			}
			@Override
			public void keyPressed(final java.awt.event.KeyEvent e) {
				fireEvent(KeyEvent.Type.KEY_PRESSED, e);
			}
		});
	}

	private class KeyEventImpl implements KeyEvent<T> {

		private final Type type;
		private final java.awt.event.KeyEvent event;
		public KeyEventImpl(final Type type, final java.awt.event.KeyEvent e) {
			this.type = type;
			this.event = e;
		}

		@Override
		public java.awt.event.KeyEvent getEvent() {
			return event;
		}

		@Override
		public KeyEvent.Type getType() {
			return type;
		}

		@Override
		public T getValue() {
			return adapter.getValue();
		}
		
	}
	
	private void fireEvent(final KeyEvent.Type type, final java.awt.event.KeyEvent e) {
		final KeyEvent<T> impl = new KeyEventImpl(type, e);
 		for (final HasKeyEvent.KeyListener<T> listener: listeners) {
			listener.onKeyEvent(impl);
		}
	}
	
	@Override
	public void addKeyEventListener(final HasKeyEvent.KeyListener<T> listener) {
		listeners.add(listener);
	}

	@Override
	public void removeKeyEventListener(final HasKeyEvent.KeyListener<T> listener) {
		listeners.remove(listener);
	}
}
