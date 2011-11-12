package org.rosuda.ui.handler;

import org.rosuda.ui.context.UIContext;
import org.rosuda.ui.core.mvc.MessageBus;
import org.rosuda.ui.event.QuitEvent;

public class QuitEventHandler extends MessageBus.EventListener<QuitEvent>{
	
	private UIContext context;
	
	public QuitEventHandler(final UIContext eventContext) {
		this.context = eventContext;
	}
	
	@Override
	public void onEvent(final QuitEvent event) {
		context.getUIFrame().setVisible(false);
		System.exit(0);
	}

}
