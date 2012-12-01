package org.rosuda.ui.mmi;

import javax.swing.ListSelectionModel;

import org.rosuda.mvc.swing.TypedDynamicListModel;
import org.rosuda.ui.core.mvc.MVP;
import org.rosuda.visualizer.NodeTreeModel;

public class MMIToolModel<T> implements MVP.Model {

    private NodeTreeModel<T> uniqueStructure;
    private MMIDynamicTableModel<T> tableModel;
    private TypedDynamicListModel<String> expressionListModel;
    private ListSelectionModel expressionListSelectionModel;
    
    NodeTreeModel<T> getUniqueStructure() {
	return uniqueStructure;
    }

    void setUniqueStructure(NodeTreeModel<T> uniqueStructure) {
	this.uniqueStructure = uniqueStructure;
    }

    MMIDynamicTableModel<T> getTableModel() {
	return tableModel;
    }

    void setTableModel(MMIDynamicTableModel<T> tableModel) {
	this.tableModel = tableModel;
    }

    public ListSelectionModel getExpressionListSelectionModel() {
	return expressionListSelectionModel;
    }
    
    void setExpressionListSelectionModel(ListSelectionModel expressionListSelectionModel) {
	this.expressionListSelectionModel = expressionListSelectionModel;
    }
    
    public TypedDynamicListModel<String> getExpressionListModel() {
	return expressionListModel;
    }

    void setExpressionListModel(TypedDynamicListModel<String> expressionListModel) {
	this.expressionListModel = expressionListModel;
    }
}
