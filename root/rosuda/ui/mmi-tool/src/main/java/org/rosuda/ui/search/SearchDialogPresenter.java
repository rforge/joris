package org.rosuda.ui.search;

import java.awt.Window;

import org.rosuda.ui.core.mvc.HasClickable.ClickEvent;
import org.rosuda.ui.core.mvc.HasClickable.ClickListener;
import org.rosuda.ui.core.mvc.MVP;
import org.rosuda.ui.core.mvc.MessageBus;
import org.rosuda.ui.event.ModelSearchEvent;
import org.rosuda.ui.listener.WindowCloseListener;

public class SearchDialogPresenter implements MVP.Presenter<SearchDialogModel,SearchDialogView<?>>{

    private ClickListener searchListener;
    
    @Override
    public void bind(final SearchDialogModel model, final SearchDialogView<?> view, final MessageBus messageBus) {
	view.getTree().setValue(model.getSearchTreeModel());
	if (view.getContainer() instanceof Window) {
	    final Window container = (Window) view.getContainer();
	    view.getCloseButton().addClickListener(new WindowCloseListener(messageBus, container));
	}
	searchListener = new ClickListener() {  
	    @Override
	    public void onClick(ClickEvent event) {
		messageBus.fireEvent(new ModelSearchEvent(model.getSearchTreeModel().getConstraints()));
	    }
	};
	view.getSearchButton().addClickListener(searchListener);	
    }

    @Override
    public void unbind(final SearchDialogModel model, final SearchDialogView<?> view, final MessageBus messageBus) {
	if (searchListener != null) {
	    view.getSearchButton().removeClickListener(searchListener);
	}	
    }

}
