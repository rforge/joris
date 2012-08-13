package org.rosuda.ui.mmi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JDialog;

import org.rosuda.irconnect.IREXP;
import org.rosuda.type.Node;
import org.rosuda.ui.context.UIContext;
import org.rosuda.ui.core.mvc.MessageBus;
import org.rosuda.ui.dialog.RootNodeWrapper;
import org.rosuda.visualizer.NodeTreeModel;

public class IRMMISpreadSheet extends JDialog {

    private final List<String> paths = new ArrayList<String>();

    public class JDialogImpl {

	private final MMIToolPresenter presenter;
	private final MMIToolModel model;
	private final MMIToolViewDialogImpl view;

	public JDialogImpl(UIContext context, final Collection<Node<IREXP>> data) throws Exception {
	    this.presenter = new MMIToolPresenter();
	    this.model = new MMIToolModel();
	    this.model.setUniqueStructure(new NodeTreeModel<IREXP>(new RootNodeWrapper(null, data)));
	    // TODO: push panel, input and SwingEngine .. to viewImpl!
	    this.view = new MMIToolViewDialogImpl(context);
	    presenter.bind(model, view, context.getAppContext().getBean(MessageBus.class));
	}

    }

    public IRMMISpreadSheet(final UIContext context, final Collection<Node<IREXP>> data) throws Exception {
	super(context.getUIFrame(), ModalityType.MODELESS);
	if (data.isEmpty()) {
	    // TODO show empty warning dialog
	} else {
	    final JDialogImpl jDialogImpl = new JDialogImpl(context, data);
	}
    }
}
