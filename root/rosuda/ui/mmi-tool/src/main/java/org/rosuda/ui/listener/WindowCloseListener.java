package org.rosuda.ui.listener;

import java.awt.Window;

import org.rosuda.ui.core.mvc.HasClickable.ClickEvent;
import org.rosuda.ui.core.mvc.HasClickable.ClickListener;
import org.rosuda.ui.core.mvc.MessageBus;
import org.rosuda.ui.event.CloseEvent;

public class WindowCloseListener extends MessageBusListener implements ClickListener{

	private final Window window;
	
	public WindowCloseListener(final MessageBus messageBus, final Window window) {
		super(messageBus);
		this.window = window;
	}


	@Override
	public void onClick(ClickEvent event) {
	    messageBus.fireEvent(new CloseEvent(window));
		window.setVisible(false);
		window.dispose();
	}
	
	
}
