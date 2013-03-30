package org.rosuda.ui.handler;

import org.rosuda.ui.core.mvc.MessageBus;
import org.rosuda.ui.event.StoreSelectionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StoreSelectionEventHandler extends MessageBus.EventListener<StoreSelectionEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScanWorkspaceEventHandler.class);

    @Override
    public void onEvent(StoreSelectionEvent selectionEvent) {
        // TODO Auto-generated method stub
        LOGGER.info("selected ..");
    }

}
