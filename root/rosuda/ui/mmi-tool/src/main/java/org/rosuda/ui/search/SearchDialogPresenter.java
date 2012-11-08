package org.rosuda.ui.search;

import java.awt.Window;

import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import org.rosuda.ui.core.mvc.HasClickable.ClickEvent;
import org.rosuda.ui.core.mvc.HasClickable.ClickListener;
import org.rosuda.ui.core.mvc.MVP;
import org.rosuda.ui.core.mvc.MessageBus;
import org.rosuda.ui.event.ModelSearchEvent;
import org.rosuda.ui.listener.WindowCloseListener;
import org.rosuda.ui.search.SearchDataNode.ConstraintType;

public class SearchDialogPresenter<C> implements MVP.Presenter<SearchDialogModel, SearchDialogView<C>> {

    private ClickListener searchListener;
    private ClickListener addListener;
    private TreeSelectionListener selectedNodeListener;

    @Override
    public void bind(final SearchDialogModel model, final SearchDialogView<C> view, final MessageBus messageBus) {
	view.getTreeTableModel().setValue(model.getSearchTreeModel());
	if (view.getViewContainer() instanceof Window) {
	    final Window container = (Window) view.getViewContainer();
	    view.getCloseButton().addClickListener(new WindowCloseListener(messageBus, container));
	}
	searchListener = new ClickListener() {
	    @Override
	    public void onClick(ClickEvent event) {
		messageBus.fireEvent(new ModelSearchEvent(model.getSearchTreeModel().getConstraints()));
	    }
	};

	addListener = new ClickListener() {
	    @Override
	    public void onClick(ClickEvent event) {
		// wie merkts mein model ?
		final TreePath[] pathToParent = view.getTreeSelectionModel().getValue().getSelectionPaths();
		// TODO get selected path and add here ...
		// model.getSearchTreeModel()
		final String nodeName = view.getNodeNameInput().getValue();
		final ConstraintType constraintType = view.getNodeConstraintType().getValue();
		final SearchDataNode node = new SearchDataNode(nodeName, constraintType);
		if (pathToParent == null) {
		    if (model.getSearchTreeModel().getRoot() == null) {
			model.getSearchTreeModel().setRoot(node);
		    }
		    // create root
		} else {
		    for (final TreePath path : pathToParent) {
			SearchDataNode parentDataNode = (SearchDataNode) path.getLastPathComponent();
			parentDataNode.addChild(node);
		    }
		}
	    }
	};
	view.getSearchButton().addClickListener(searchListener);
	view.getAddToTree().addClickListener(addListener);
    }

    @Override
    public void unbind(final SearchDialogModel model, final SearchDialogView<C> view, final MessageBus messageBus) {
	if (searchListener != null) {
	    view.getSearchButton().removeClickListener(searchListener);
	}
    }

}
