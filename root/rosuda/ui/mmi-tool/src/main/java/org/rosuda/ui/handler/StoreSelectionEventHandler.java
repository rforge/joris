package org.rosuda.ui.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rosuda.ui.context.UIContext;
import org.rosuda.ui.context.UIContextAware;
import org.rosuda.ui.core.mvc.MessageBus;
import org.rosuda.ui.event.StoreSelectionEvent;

public class StoreSelectionEventHandler extends
MessageBus.EventListener<StoreSelectionEvent> implements UIContextAware{

	private static final Log LOG = LogFactory.getLog(ScanWorkspaceEventHandler.class);
	
	private UIContext context;
	
	@Override
	public void onEvent(StoreSelectionEvent selectionEvent) {
		// TODO Auto-generated method stub
		LOG.info("selected ..");
	}

	@Override
	public void setUIContext(final UIContext context) {
		this.context = context;
	}
}
