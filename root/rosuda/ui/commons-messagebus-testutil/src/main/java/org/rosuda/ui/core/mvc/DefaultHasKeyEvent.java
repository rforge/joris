package org.rosuda.ui.core.mvc;

import java.util.ArrayList;
import java.util.List;

import org.rosuda.ui.core.mvc.HasKeyEvent.KeyEvent.Type;

public class DefaultHasKeyEvent implements HasKeyEvent{

    private List<HasKeyEvent.KeyListener> listeners = new ArrayList<HasKeyEvent.KeyListener>();
    
    @Override
    public void addKeyEventListener(HasKeyEvent.KeyListener listener) {
	listeners.add(listener);
    }

    @Override
    public void removeKeyEventListener(HasKeyEvent.KeyListener listener) {
	listeners.remove(listener);
    }

    @Override
    public void sendEvent(final Type type, final int modifiers, final int keyCode) {
	for (final HasKeyEvent.KeyListener listener: listeners) {
	    listener.onKeyEvent(new HasKeyEvent.KeyEvent(type, modifiers, keyCode));
	}
    }

}
