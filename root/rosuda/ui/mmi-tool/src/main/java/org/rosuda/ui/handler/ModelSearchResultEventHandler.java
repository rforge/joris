package org.rosuda.ui.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rosuda.irconnect.IREXP;
import org.rosuda.ui.context.Aware;
import org.rosuda.ui.context.UIContext;
import org.rosuda.ui.core.mvc.MessageBus;
import org.rosuda.ui.event.ModelSearchResultEvent;
import org.rosuda.ui.mmi.IRMMISpreadSheet;
import org.springframework.stereotype.Component;

@Component
public class ModelSearchResultEventHandler extends
	MessageBus.EventListener<ModelSearchResultEvent> implements Aware<UIContext>{

    private static final Log LOG = LogFactory.getLog(ModelSearchResultEventHandler.class);
    private UIContext uiContext;
    
    @Override
    public void onEvent(ModelSearchResultEvent event) {
	try {
	    //TODO <T>
	    new IRMMISpreadSheet<IREXP>(uiContext, event.getResult());
	} catch (Exception e) {
	    LOG.error("could not create spreadsheet", e);
	}
    }

    @Override
    public void setContext(UIContext context) {
	this.uiContext = context;
    }

}
