package org.rosuda.ui.mmi;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.TableModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTree;
import org.rosuda.mvc.swing.JButtonHasClickable;
import org.rosuda.mvc.swing.JListHasSelectionModel;
import org.rosuda.mvc.swing.JListHasValue;
import org.rosuda.mvc.swing.TypedDynamicListModel;
import org.rosuda.type.NodePath;
import org.rosuda.ui.SwingLayoutProcessor;
import org.rosuda.ui.context.UIContext;
import org.rosuda.ui.core.mvc.HasClickable;
import org.rosuda.ui.core.mvc.HasValue;
import org.rosuda.ui.core.mvc.impl.AbstractHasValue;
import org.rosuda.visualizer.NodeTreeModel;
import org.rosuda.visualizer.NodeTreeSelection;

public class MMIToolViewDialogImpl<T> extends JDialog implements MMIToolView<T, JDialog> {

    private static final long serialVersionUID = 1145922204879696649L;
    public AbstractButton synchronizeTreeToTable;
    public JXTree multiselector;
    public JXTable valuetable;
    public JTextField expressionField;
    public JList expressionList;
    public AbstractButton createExpressionFieldButton;
    private final UniqueStructureTree uniqueStructureTree;
    private final MMITable mmiTable;
    private final HasClickable synchronizeClickable;
    private SelectionModel selectionModel;
    private TypedDynamicListModel<String> expressionListModel;
    private HasValue<String> expressionFieldModel;
    private HasClickable createExpressionFieldClickable;
    private HasValue<TypedDynamicListModel<String>> expressionListModelHasValue;
    private HasValue<ListSelectionModel> expressionListSelectionModel;
    
    private class UniqueStructureTree extends AbstractHasValue<NodeTreeModel<T>> {	
	@SuppressWarnings("unchecked")
	@Override
	public NodeTreeModel<T> getValue() {
	    final TreeModel model = multiselector.getModel();
	    if (model instanceof NodeTreeModel) {
		return (NodeTreeModel<T>) model;
	    }
	    return null;
	}

	@Override
	protected void onValueChange(final NodeTreeModel<T> newValue) {
	    multiselector.setModel(newValue);
	}
    }

    private class MMITable extends AbstractHasValue<TableModel> {

	@Override
	public TableModel getValue() {
	    return valuetable.getModel();
	}

	@Override
	protected void onValueChange(final TableModel newTableModel) {
	    valuetable.setModel(newTableModel);
	}

    }

    private class SelectionModel extends AbstractHasValue<NodeTreeSelection> implements TreeSelectionListener{

	private SelectionModel() {
	    multiselector.addTreeSelectionListener(this);
	}
	@Override
	public NodeTreeSelection getValue() {
	    final List<NodePath> paths = new ArrayList<NodePath>();
	    //TODO convert to list<NodePath<IREXP>>
	    for (final TreePath selectedPath : multiselector.getSelectionPaths()) {
		//TODO check if that is the RIGHT interface (regarding INDEX)
		paths.add(NodePath.Impl.parse(selectedPath));
	    }	    
	    return new NodeTreeSelection.Impl(paths);
	}
	@Override
	public void valueChanged(TreeSelectionEvent e) {
	    fireChangeEvent(getValue());
	}

    }
    
    private static class TypedDynamicListModelHasValue<T> extends AbstractHasValue<TypedDynamicListModel<T>> {

	private final JList expressionList;

	public TypedDynamicListModelHasValue(JList expressionList) {
	    this.expressionList = expressionList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public TypedDynamicListModel<T> getValue() {
	    return ((TypedDynamicListModel<T>) expressionList.getModel());
	}
	
	@Override
	protected void onValueChange(TypedDynamicListModel<T> newValue) {
	    expressionList.setModel(newValue);
	}
    }
    
    private class ExpressionFieldModel extends AbstractHasValue<String> {

	@Override
	public String getValue() {
	    return expressionField.getText();
	}
	
    }
    
    public MMIToolViewDialogImpl(UIContext context) throws Exception {
	super(context.getUIFrame(), ModalityType.MODELESS);
	SwingLayoutProcessor.processLayout(this, "/gui/dialog/MMISpreadSheetDialog.xml");
	this.uniqueStructureTree = new UniqueStructureTree();
	this.mmiTable = new MMITable();
	this.synchronizeClickable = new JButtonHasClickable(synchronizeTreeToTable);
	this.selectionModel = new SelectionModel();
	this.expressionFieldModel = new ExpressionFieldModel();
	this.createExpressionFieldClickable = new JButtonHasClickable(createExpressionFieldButton);
	final JListHasValue<String> listModel = new JListHasValue<String>(expressionList);
	this.expressionListModelHasValue = new TypedDynamicListModelHasValue<String>(expressionList);
	expressionList.setModel(listModel);
	this.expressionListSelectionModel = new JListHasSelectionModel(expressionList);
    }

    @Override
    public HasValue<NodeTreeModel<T>> getUniqueStructureTree() {
	return uniqueStructureTree;
    }

    @Override
    public HasValue<TableModel> getMMITable() {
	return mmiTable;
    }

    @Override
    public HasClickable getSynchronizeTreeToTable() {
	return synchronizeClickable;
    }

    @Override
    public HasValue<NodeTreeSelection> getUniqueStructureSelection() {
	return selectionModel;
    }

    @Override
    public JDialog getViewContainer() {
	return this;
    }

    @Override
    public HasValue<String> getExpressionField() {
        return expressionFieldModel;
    }
    
    @Override
    public HasClickable getCreateExpressionButton() {
        return createExpressionFieldClickable;
    }

    @Override
    public HasValue<TypedDynamicListModel<String>> getExpressionListModel() {
        return expressionListModelHasValue;
    }

    @Override
    public HasValue<ListSelectionModel> getExpressionListSelection() {
	return expressionListSelectionModel;
    }
}
