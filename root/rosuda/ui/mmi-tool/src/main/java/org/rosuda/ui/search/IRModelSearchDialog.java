package org.rosuda.ui.search;

import javax.swing.JDialog;

import org.rosuda.ui.context.UIContext;
import org.rosuda.ui.core.mvc.MessageBus;

public class IRModelSearchDialog<C extends JDialog> {

    public class JDialogImpl {

	private final SearchDialogPresenter<C> presenter;
	private final SearchDialogModel model;
	private final SearchDialogViewJDialogImpl<C> view;

	public JDialogImpl(UIContext context) throws Exception {
	    this.presenter = new SearchDialogPresenter<C>();
	    this.model = new SearchDialogModel();
	    // TODO: push panel, input and SwingEngine .. to viewImpl!
	    this.view = new SearchDialogViewJDialogImpl<C>(context);
	    presenter.bind(model, view, context.getAppContext().getBean(MessageBus.class));
	}

    }

    public IRModelSearchDialog(final UIContext context) throws Exception {
	new JDialogImpl(context).view.render();
    }
}
