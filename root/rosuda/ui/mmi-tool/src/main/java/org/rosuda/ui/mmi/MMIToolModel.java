package org.rosuda.ui.mmi;

import org.rosuda.ui.core.mvc.MVP;
import org.rosuda.visualizer.NodeTreeModel;

public class MMIToolModel<T> implements MVP.Model {

    private NodeTreeModel<T> uniqueStructure;
    private MMIDynamicTableModel<T> tableModel;

    NodeTreeModel<T> getUniqueStructure() {
	return uniqueStructure;
    }

    void setUniqueStructure(NodeTreeModel<T> uniqueStructure) {
	this.uniqueStructure = uniqueStructure;
    }

    public MMIDynamicTableModel<T> getTableModel() {
	return tableModel;
    }

    public void setTableModel(MMIDynamicTableModel<T> tableModel) {
	this.tableModel = tableModel;
    }

}
