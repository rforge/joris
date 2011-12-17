package org.rosuda.ui.listener;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.rosuda.ui.core.mvc.MessageBus;
import org.rosuda.ui.event.CloseEvent;

public class WindowCloseListener extends MessageBusListener implements ActionListener{

	private final Window window;
	
	public WindowCloseListener(final MessageBus messageBus, final Window window) {
		super(messageBus);
		this.window = window;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		messageBus.fireEvent(new CloseEvent(window));
		window.setVisible(false);
		window.dispose();
	}
	
	
}
