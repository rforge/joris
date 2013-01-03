package org.rosuda.ui.core.mvc;

public interface HasKeyEvent {

    public class KeyEvent {
	
	private final Type type;
	private final int keyCode;
	private final int modifiers;
	
	public enum Type {
	    KEY_UP, KEY_DOWN, KEY_PRESSED
	}

	public KeyEvent(final Type type, final int modifiers, final int keyCode) {
	    this.type = type;
	    this.modifiers = modifiers;
	    this.keyCode = keyCode;
	}
	
	public Type getType() {
	    return type;
	}
	
	public int getKeyCode() {
	    return keyCode;
	}
	
	public int getModifiers() {
	    return modifiers;
	}
	
    }

    public void addKeyEventListener(final KeyListener listener);

    public void removeKeyEventListener(final KeyListener listener);

    public interface KeyListener {
	public void onKeyEvent(final KeyEvent event);
    }

    public void sendEvent(final KeyEvent.Type type, final int modifiers, final int keyCode);
}
