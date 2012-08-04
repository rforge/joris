package org.rosuda.ui.handler;

import org.rosuda.ui.context.UIContext;
import org.rosuda.ui.context.Aware;
import org.rosuda.ui.core.mvc.MessageBus;
import org.rosuda.ui.event.QuitEvent;

public class QuitEventHandler extends MessageBus.EventListener<QuitEvent> implements Aware<UIContext>{
	
	private UIContext context;
		
	@Override
	public void onEvent(final QuitEvent event) {
		context.getUIFrame().setVisible(false);
		System.exit(0);
	}

	@Override
	public void setContext(final UIContext context) {
		this.context = context;	
	}

}
