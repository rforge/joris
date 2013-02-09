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
    private ClickListener removeListener;
    private TreeSelectionListener selectedNodeListener;

    @Override
    public void bind(final SearchDialogModel model, final SearchDialogView<C> view, final MessageBus messageBus) {
	view.getTreeTableModel().setValue(model.getSearchTreeModel());
	view.getTreeSelectionModel().setValue(model.getTreeSelectionModel());
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
	addListener = new SearchNodeTreeClickListener<C>(view, new SearchDataNodeTemplate() {
	    @Override
	    public void doWithNode(TreePath[] pathToParent, String nodeName, ConstraintType constraintType) {
		if (pathToParent == null) {
		    if (model.getSearchTreeModel().getRoot() == null) {
			final SearchDataNode aNewChild = new SearchDataNode(nodeName, constraintType);
			model.getSearchTreeModel().setRoot(aNewChild);
			model.getSearchTreeModel().addedChild(null, 0, aNewChild);
		    }
		} else {
		    for (final TreePath path : pathToParent) {
			SearchDataNode parentDataNode = (SearchDataNode) path.getLastPathComponent();
			final SearchDataNode aNewChild = new SearchDataNode(nodeName, constraintType);
			int childIndex = parentDataNode.getChildren().size();
			parentDataNode.addChild(aNewChild);
			model.getSearchTreeModel().addedChild(path, childIndex, aNewChild);
		    }
		}
	    }
	});
	
	removeListener = new SearchNodeTreeClickListener<C>(view, new SearchDataNodeTemplate() {
	    
	    @Override
	    public void doWithNode(TreePath[] pathToParent, String nodeName, ConstraintType constraintType) {
		if (pathToParent != null) {
		    for (final TreePath path : pathToParent) {
			final SearchDataNode dataNodeToRemove = (SearchDataNode) path.getLastPathComponent();
			final TreePath parentPath = path.getParentPath();
			final SearchDataNode parentNode = parentPath != null ? (SearchDataNode) parentPath.getLastPathComponent() : null;
			if (parentNode != null) {	
			    final int childIndex = model.getSearchTreeModel().getIndexOfChild(parentNode, dataNodeToRemove);
			    parentNode.removeChild(dataNodeToRemove);
			    model.getSearchTreeModel().removedChild(parentPath, childIndex, dataNodeToRemove);
			}
		    }
		}
	    }
	});
	view.getSearchButton().addClickListener(searchListener);
	view.getAddToTree().addClickListener(addListener);
	view.getRemoveFromTree().addClickListener(removeListener);
    }

    @Override
    public void unbind(final SearchDialogModel model, final SearchDialogView<C> view, final MessageBus messageBus) {
	if (searchListener != null) {
	    view.getSearchButton().removeClickListener(searchListener);
	}
    }

    private static class SearchNodeTreeClickListener<C> implements ClickListener {

	private final SearchDialogView<C> view;
	private final SearchDataNodeTemplate processSelectedTreePath;

	private SearchNodeTreeClickListener(final SearchDialogView<C> view, final SearchDataNodeTemplate processSelectedTreePath) {
	    this.view = view;
	    this.processSelectedTreePath = processSelectedTreePath;
	}

	@Override
	public void onClick(ClickEvent event) {
	    if (view.getTreeSelectionModel().getValue().isSelectionEmpty()) {
		processSelectedTreePath.doWithNode(null, view.getNodeNameInput().getValue(), view.getNodeConstraintType().getValue());
	    } else {
		final TreePath[] pathToParent = view.getTreeSelectionModel().getValue().getSelectionPaths();
		processSelectedTreePath.doWithNode(pathToParent, view.getNodeNameInput().getValue(), view.getNodeConstraintType().getValue());
	    }
	}
    }

    private static interface SearchDataNodeTemplate {

	void doWithNode(final TreePath[] pathToParent, final String nodeName, final ConstraintType constraintType);

    }

}
