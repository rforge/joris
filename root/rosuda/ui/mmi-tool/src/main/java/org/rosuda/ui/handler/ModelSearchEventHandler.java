package org.rosuda.ui.handler;

import org.springframework.stereotype.Component;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rosuda.graph.service.GraphService;
import org.rosuda.irconnect.IREXP;
import org.rosuda.type.Node;
import org.rosuda.ui.context.UIContext;
import org.rosuda.ui.context.UIContextAware;
import org.rosuda.ui.core.mvc.MessageBus;
import org.rosuda.ui.event.ModelSearchEvent;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class ModelSearchEventHandler extends
	MessageBus.EventListener<ModelSearchEvent> implements UIContextAware{

	private static Log LOG = LogFactory.getLog(ModelSearchEventHandler.class);
	@Autowired
	private GraphService<IREXP> dataService;
	private UIContext context;

	@Override
	public void setUIContext(final UIContext context) {
		this.context = context;
	}

	@Override
	public void onEvent(ModelSearchEvent event) {
		// TODO Auto-generated method stub
		final List<Node<IREXP>> result = dataService.find(event.getConstraints());
		// TODO open result dialog
		LOG.info("found nodes "+result);
	}

	
}
