package org.rosuda.mvc.swing;

import java.util.ArrayList;
import java.util.List;

import javax.swing.text.JTextComponent;

import org.rosuda.ui.core.mvc.HasKeyEvent;
import org.rosuda.ui.core.mvc.HasKeyEvent.KeyEvent.Type;

public class JTextComponentHasKeyEvent implements HasKeyEvent {

    private final List<HasKeyEvent.KeyListener> listeners = new ArrayList<HasKeyEvent.KeyListener>();

    public JTextComponentHasKeyEvent(final JTextComponent textComponent) {
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

    private void fireEvent(final KeyEvent.Type type, final java.awt.event.KeyEvent e) {
	fireEvent(createKeyEvent(type, e.getModifiers(), e.getKeyCode()));
    }

    private void fireEvent(final KeyEvent impl) {
	for (final HasKeyEvent.KeyListener listener : listeners) {
	    listener.onKeyEvent(impl);
	}
    }

    @Override
    public void addKeyEventListener(final HasKeyEvent.KeyListener listener) {
	listeners.add(listener);
    }

    @Override
    public void removeKeyEventListener(final HasKeyEvent.KeyListener listener) {
	listeners.remove(listener);
    }

    @Override
    public void sendEvent(final Type type, final int modifiers, final int keyCode) {
       	fireEvent(createKeyEvent(type, modifiers, keyCode));
        
    }

    private KeyEvent createKeyEvent(final KeyEvent.Type type, final int modifiers, final int keyCode) {
	return new KeyEvent(type, modifiers, keyCode);
    }
}
