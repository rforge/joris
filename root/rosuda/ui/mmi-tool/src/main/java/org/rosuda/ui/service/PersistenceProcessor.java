package org.rosuda.ui.service;

import org.rosuda.graph.service.GraphService;
import org.rosuda.irconnect.IREXP;
import org.rosuda.type.Node;
import org.rosuda.ui.core.mvc.MessageBus;
import org.rosuda.ui.event.StoreSelectionEvent;
import org.rosuda.ui.work.WrapIREXPAsNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersistenceProcessor {

	@Autowired
	private GraphService<IREXP> graphService;
	@Autowired
	public void setMessageBus(MessageBus messageBus) {
		messageBus.registerListener(new MessageBus.EventListener<StoreSelectionEvent>() {
			@Override
			public void onEvent(StoreSelectionEvent event) {
				for (final Node<IREXP> rexpNode : event.getRObjects()) {
					graphService.store(rexpNode);
				}
			}
		});
	}

	public PersistenceProcessor() {
	}
}
