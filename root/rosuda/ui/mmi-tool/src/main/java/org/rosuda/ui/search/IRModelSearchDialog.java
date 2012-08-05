package org.rosuda.ui.search;

import org.rosuda.ui.context.UIContext;
import org.rosuda.ui.core.mvc.MessageBus;

public class IRModelSearchDialog {

    private static final long serialVersionUID = -4983090346927958415L;

    public class JDialogImpl {

	private final SearchDialogPresenter presenter;
	private final SearchDialogModel model;
	private final SearchDialogViewJDialogImpl view;

	public JDialogImpl(UIContext context) throws Exception {
	    this.presenter = new SearchDialogPresenter();
	    this.model = new SearchDialogModel();
	    // TODO: push panel, input and SwingEngine .. to viewImpl!
	    this.view = new SearchDialogViewJDialogImpl(context);
	    presenter.bind(model, view, context.getAppContext().getBean(MessageBus.class));
	}

    }

    public IRModelSearchDialog(final UIContext context) throws Exception {
	new JDialogImpl(context).view.render();
    }
}
