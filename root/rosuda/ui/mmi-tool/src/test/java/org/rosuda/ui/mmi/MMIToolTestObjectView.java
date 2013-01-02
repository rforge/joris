package org.rosuda.ui.mmi;

import javax.swing.ListSelectionModel;
import javax.swing.table.TableModel;

import org.rosuda.irconnect.IREXP;
import org.rosuda.mvc.swing.TypedDynamicListModel;
import org.rosuda.ui.core.mvc.DefaultHasClickable;
import org.rosuda.ui.core.mvc.DefaultHasValue;
import org.rosuda.ui.core.mvc.DefaultTestView;
import org.rosuda.ui.core.mvc.HasClickable;
import org.rosuda.ui.core.mvc.HasValue;
import org.rosuda.visualizer.NodeTreeModel;
import org.rosuda.visualizer.NodeTreeSelection;

class MMIToolTestObjectView extends DefaultTestView implements MMIToolView<IREXP, Object> {

    private HasValue<NodeTreeModel<IREXP>> uniqueStructureTree = new DefaultHasValue<NodeTreeModel<IREXP>>();
    private HasValue<NodeTreeSelection> uniqueStructureSelection = new DefaultHasValue<NodeTreeSelection>();
    private HasValue<TableModel> mmiTable = new DefaultHasValue<TableModel>();
    private final HasClickable synchronizeTreeToTable = new DefaultHasClickable();
    private HasValue<String> expressionField = new DefaultHasValue<String>();
    private HasClickable expressionButton = new DefaultHasClickable();
    private HasValue<ListSelectionModel> expressionListSelection = new DefaultHasValue<ListSelectionModel>();
    private HasValue<TypedDynamicListModel<String>> expressionListModel = new DefaultHasValue<TypedDynamicListModel<String>>();

    @Override
    public HasValue<NodeTreeModel<IREXP>> getUniqueStructureTree() {
	return uniqueStructureTree;
    }

    @Override
    public HasValue<NodeTreeSelection> getUniqueStructureSelection() {
	return uniqueStructureSelection;
    }

    @Override
    public HasValue<TableModel> getMMITable() {
	return mmiTable;
    }

    @Override
    public HasClickable getSynchronizeTreeToTable() {
	return synchronizeTreeToTable;
    }

    @Override
    public HasValue<String> getExpressionField() {
	return expressionField;
    }

    @Override
    public HasClickable getCreateExpressionButton() {
	return expressionButton;
    }

    @Override
    public HasValue<ListSelectionModel> getExpressionListSelection() {
	return expressionListSelection;
    }

    @Override
    public HasValue<TypedDynamicListModel<String>> getExpressionListModel() {
	return expressionListModel;
    }

}
