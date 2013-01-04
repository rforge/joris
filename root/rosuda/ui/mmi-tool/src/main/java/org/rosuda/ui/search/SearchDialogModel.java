package org.rosuda.ui.search;

import javax.swing.table.TableModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeSelectionModel;

import org.rosuda.graph.service.search.VertexConstraint;
import org.rosuda.ui.core.mvc.MVP;


public class SearchDialogModel implements MVP.Model{

    private SearchTreeModel searchTreeModel;
    private TreeSelectionModel treeSelectionModel = new DefaultTreeSelectionModel();
    
    void setSearchTreeModel(SearchTreeModel searchTreeModel) {
	this.searchTreeModel = searchTreeModel;
    }

    SearchTreeModel getSearchTreeModel() {
	return searchTreeModel;
    }
    
    Iterable<VertexConstraint> getConstraints() {
	return searchTreeModel.getConstraints();
    }

    public TableModel getSearchTableModel() {
	return searchTreeModel;
    }

    public TreeSelectionModel getTreeSelectionModel() {
	return treeSelectionModel ;
    }

}
