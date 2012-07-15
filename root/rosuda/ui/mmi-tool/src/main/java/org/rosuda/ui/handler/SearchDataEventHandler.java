package org.rosuda.ui.handler;

import org.rosuda.ui.context.UIContext;
import org.rosuda.ui.context.UIContextAware;
import org.rosuda.ui.core.mvc.MessageBus;
import org.rosuda.ui.dialog.IRModelSearchDialog;
import org.rosuda.ui.event.SearchDataEvent;
import org.springframework.stereotype.Component;

@Component
public class SearchDataEventHandler extends
		MessageBus.EventListener<SearchDataEvent> implements UIContextAware{

	private UIContext context;

	public SearchDataEventHandler() {
	}

	@Override
	public void onEvent(final SearchDataEvent event) {
		try {
			new IRModelSearchDialog(context).showNewDialog();
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setUIContext(final UIContext context) {
		this.context = context;
	}
}
