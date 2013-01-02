package org.rosuda.ui.search;

import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeSelectionModel;

import org.jdesktop.swingx.treetable.TreeTableModel;
import org.rosuda.ui.core.mvc.DefaultHasClickable;
import org.rosuda.ui.core.mvc.DefaultHasValue;
import org.rosuda.ui.core.mvc.DefaultTestView;
import org.rosuda.ui.core.mvc.HasClickable;
import org.rosuda.ui.core.mvc.HasValue;
import org.rosuda.ui.search.SearchDataNode.ConstraintType;

public class SearchDialogTestObjectView extends DefaultTestView implements SearchDialogView<Object> {

    private final HasClickable closeButton = new DefaultHasClickable();
    private final HasClickable searchButton = new DefaultHasClickable();
    private final HasClickable add = new DefaultHasClickable();
    private final HasClickable remove = new DefaultHasClickable();
    private final HasValue<String> name = new DefaultHasValue<String>();
    private final HasValue<TreeTableModel> treeTableModel = new DefaultHasValue<TreeTableModel>();
    private final HasValue<TreeSelectionModel> treeSelectionModel = new HasValue<TreeSelectionModel>() {

	final TreeSelectionModel model = new DefaultTreeSelectionModel();

	@Override
	public TreeSelectionModel getValue() {
	    return model;
	}

	@Override
	public void setValue(TreeSelectionModel value) {
	    throw new UnsupportedOperationException();
	}

	@Override
	public void addChangeListener(org.rosuda.ui.core.mvc.HasValue.ValueChangeListener<TreeSelectionModel> listener) {
	}

	@Override
	public void removeChangeListener(org.rosuda.ui.core.mvc.HasValue.ValueChangeListener<TreeSelectionModel> listener) {
	}

    };
    private final HasValue<ConstraintType> constraint = new DefaultHasValue<ConstraintType>();

    @Override
    public HasClickable getCloseButton() {
	return closeButton;
    }

    @Override
    public HasClickable getSearchButton() {
	return searchButton;
    }

    @Override
    public HasClickable getAddToTree() {
	return add;
    }

    @Override
    public HasClickable getRemoveFromTree() {
	return remove;
    }

    @Override
    public HasValue<String> getNodeNameInput() {
	return name;
    }

    @Override
    public HasValue<ConstraintType> getNodeConstraintType() {
	return constraint;
    }

    @Override
    public HasValue<TreeTableModel> getTreeTableModel() {
	return treeTableModel;
    }

    @Override
    public HasValue<TreeSelectionModel> getTreeSelectionModel() {
	return treeSelectionModel;
    }

}
