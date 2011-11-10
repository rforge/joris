package org.rosuda.ui.main;

import org.rosuda.ui.core.mvc.MessageBus;

public final class CRTKeyEvent implements MessageBus.Event {

	private final String value;
	
	CRTKeyEvent(final String value) {
		this.value = value;
	}
	
	public String getValue() {
		return this.value;
	}
}
