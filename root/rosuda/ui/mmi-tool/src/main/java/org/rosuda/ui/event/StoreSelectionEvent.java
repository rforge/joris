package org.rosuda.ui.event;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.rosuda.irconnect.IREXP;
import org.rosuda.type.Node;
import org.rosuda.ui.core.mvc.MessageBus;

public class StoreSelectionEvent implements MessageBus.Event {

	private static final Collection<Node<IREXP>> EMPTY = Collections.unmodifiableCollection(new HashSet<Node<IREXP>>());
	private Collection<Node<IREXP>> rObjects;
	
	public StoreSelectionEvent(final Collection<Node<IREXP>> rObjects) {
		this.rObjects = (rObjects == null) ? EMPTY : rObjects;
	}
	
	public Collection<Node<IREXP>> getRObjects() {
		return rObjects;
	}
}
