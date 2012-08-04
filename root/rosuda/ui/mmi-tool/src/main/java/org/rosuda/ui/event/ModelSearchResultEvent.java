package org.rosuda.ui.event;

import java.util.Collection;
import java.util.Collections;

import org.rosuda.irconnect.IREXP;
import org.rosuda.type.Node;
import org.rosuda.ui.core.mvc.MessageBus;

public class ModelSearchResultEvent implements MessageBus.Event{

    private Collection<Node<IREXP>> result;
    
    public void setResult(Collection<Node<IREXP>> result) {
	this.result = result;
    }
    
    public Collection<Node<IREXP>> getResult() {
	if (result == null)
	    return Collections.emptyList();
	return result;
    }
}
