package org.rosuda.ui.main;

import org.rosuda.irconnect.IREXP;
import org.rosuda.ui.core.mvc.MessageBus;

public final class IREXPResponseEvent implements MessageBus.Event {

	private final IREXP value;
	
	public IREXPResponseEvent(final IREXP value) {
		this.value = value;
	}
	
	public IREXP getValue() {
		return this.value;
	}
}
