package org.rosuda.ui.core.mvc;

import java.util.ArrayList;
import java.util.List;

public class DefaultHasClickable implements HasClickable {

    private final List<ClickListener> clickListeners = new ArrayList<ClickListener>();
    @Override
    public final void addClickListener(final ClickListener listener) {
	clickListeners.add(listener);
    }

    @Override
    public final void removeClickListener(final ClickListener listener) {
	clickListeners.remove(listener);
    }
    
    public void click(final ClickEvent event) {
	for (final ClickListener listener : new ArrayList<ClickListener>(clickListeners)) {
	    listener.onClick(event);
	}
    }
    
}
