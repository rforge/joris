package org.rosuda.ui.event;

import java.awt.Window;

import org.rosuda.ui.core.mvc.MessageBus;

public class CloseEvent implements MessageBus.Event {

	private final Window window;

	public CloseEvent(final Window window) {
		this.window = window;
	}
	
	public Window getWindow() {
		return window;
	}
	
}
