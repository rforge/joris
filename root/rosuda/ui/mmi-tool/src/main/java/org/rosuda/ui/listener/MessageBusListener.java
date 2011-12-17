package org.rosuda.ui.listener;

import org.rosuda.ui.core.mvc.MessageBus;

public class MessageBusListener {

	protected final MessageBus messageBus;
	
	protected MessageBusListener(final MessageBus messageBus) {
		this.messageBus = messageBus;
	}
}
