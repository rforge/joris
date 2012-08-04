package org.rosuda.ui.handler;

import org.rosuda.graph.service.GraphService;
import org.rosuda.irconnect.IREXP;
import org.rosuda.ui.core.mvc.MessageBus;
import org.rosuda.ui.event.ModelSearchEvent;
import org.rosuda.ui.event.ModelSearchResultEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ModelSearchEventHandler extends MessageBus.EventListener<ModelSearchEvent> {

    @Autowired
    private GraphService<IREXP> dataService;
    private MessageBus bus;

    @Override
    public void onEvent(ModelSearchEvent event) {
	ModelSearchResultEvent result = new ModelSearchResultEvent();
	result.setResult(dataService.find(event.getConstraints()));
	bus.fireEvent(result);
    }

    @Autowired
    public void setMessageBus(MessageBus bus) {
	this.bus = bus;
    }

}
