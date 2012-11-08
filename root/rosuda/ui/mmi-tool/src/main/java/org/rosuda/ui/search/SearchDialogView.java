package org.rosuda.ui.search;

import javax.swing.tree.TreeSelectionModel;

import org.jdesktop.swingx.treetable.TreeTableModel;
import org.rosuda.ui.core.mvc.HasClickable;
import org.rosuda.ui.core.mvc.HasValue;
import org.rosuda.ui.core.mvc.MVP;
import org.rosuda.ui.search.SearchDataNode.ConstraintType;

public interface SearchDialogView<C> extends MVP.View<C> {
    
    HasClickable getCloseButton();
    HasClickable getSearchButton();
    HasValue<TreeTableModel> getTreeTableModel();
    HasValue<TreeSelectionModel> getTreeSelectionModel();
    
    HasValue<String> getNodeNameInput();
    HasValue<ConstraintType> getNodeConstraintType();
    HasClickable getAddToTree();
    HasClickable getRemoveFromTree();
}
