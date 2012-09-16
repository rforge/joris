package org.rosuda.ui.handler;

import javax.swing.JDialog;

import org.rosuda.ui.context.Aware;
import org.rosuda.ui.context.UIContext;
import org.rosuda.ui.core.mvc.MessageBus;
import org.rosuda.ui.event.SearchDataEvent;
import org.rosuda.ui.search.IRModelSearchDialog;
import org.springframework.stereotype.Component;

@Component
public class SearchDataEventHandler extends MessageBus.EventListener<SearchDataEvent> implements Aware<UIContext> {

    private UIContext context;

    public SearchDataEventHandler() {
    }

    @Override
    public void onEvent(final SearchDataEvent event) {
	try {
	    //TODO T
	    new IRModelSearchDialog<JDialog>(context);
	} catch (final Exception e) {
	    throw new RuntimeException(e);
	}
    }

    @Override
    public void setContext(final UIContext context) {
	this.context = context;
    }
}
