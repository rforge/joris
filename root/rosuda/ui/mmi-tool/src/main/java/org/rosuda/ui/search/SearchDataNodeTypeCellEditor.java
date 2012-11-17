package org.rosuda.ui.search;

import java.awt.Component;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTable;

class SearchDataNodeTypeCellEditor extends AbstractTableCellEditor {

    private final JComboBox editorComponent;

    SearchDataNodeTypeCellEditor() {
	editorComponent = new JComboBox();
	final ComboBoxModel typeSelectionModel = new DefaultComboBoxModel(SearchDataNode.ConstraintType.values());
	editorComponent.setModel(typeSelectionModel);
    }

    @Override
    public Object getCellEditorValue() {
	//TODO missing somehow to set the value!
	return editorComponent.getSelectedItem();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
	if (value != null && (value instanceof SearchDataNode.ConstraintType)) {
	    editorComponent.setSelectedItem(value);
	}
	return editorComponent;
    }

}
