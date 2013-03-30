package org.rosuda.ui.handler;

import org.rosuda.irconnect.IREXP;
import org.rosuda.ui.context.Aware;
import org.rosuda.ui.context.UIContext;
import org.rosuda.ui.core.mvc.MessageBus;
import org.rosuda.ui.event.ModelSearchResultEvent;
import org.rosuda.ui.mmi.IRMMISpreadSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ModelSearchResultEventHandler extends MessageBus.EventListener<ModelSearchResultEvent> implements Aware<UIContext> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelSearchResultEventHandler.class);
    private UIContext uiContext;

    @Override
    public void onEvent(ModelSearchResultEvent event) {
        try {
            // TODO <T>
            new IRMMISpreadSheet<IREXP>(uiContext, event.getResult());
        } catch (Exception e) {
            LOGGER.error("could not create spreadsheet", e);
        }
    }

    @Override
    public void setContext(UIContext context) {
        this.uiContext = context;
    }

}
