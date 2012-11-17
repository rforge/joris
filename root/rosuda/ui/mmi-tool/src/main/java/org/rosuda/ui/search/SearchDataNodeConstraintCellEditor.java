package org.rosuda.ui.search;

import java.awt.Component;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTable;

import org.rosuda.graph.service.search.BoolCompareType;
import org.rosuda.graph.service.search.Relation;
import org.rosuda.graph.service.search.StringCompareType;
import org.rosuda.ui.search.SearchDataNode.ConstraintType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SearchDataNodeConstraintCellEditor extends AbstractTableCellEditor {

    private static final Logger logger = LoggerFactory.getLogger(SearchDataNodeConstraintCellEditor.class);
    private final JComboBox editorComboBox;
    @SuppressWarnings("unused")
    private final ComboBoxModel numberModel;
    @SuppressWarnings("unused")
    private final ComboBoxModel stringModel;
    @SuppressWarnings("unused")
    private final ComboBoxModel booleanModel;
    @SuppressWarnings("unused")
    private final ComboBoxModel nameModel;
    private final DefaultComboBoxModel defaultModel = new DefaultComboBoxModel();
    private final Map<ConstraintType, ComboBoxModel> dynamicEditorModel;

    SearchDataNodeConstraintCellEditor() {
	editorComboBox = new JComboBox();
	numberModel = new DefaultComboBoxModel(Relation.values());
	stringModel = new DefaultComboBoxModel(StringCompareType.values());
	booleanModel = new DefaultComboBoxModel(BoolCompareType.values());
	nameModel = new DefaultComboBoxModel(new Object[] { StringCompareType.EQUALS });
	final Map<ConstraintType, ComboBoxModel> map = new HashMap<SearchDataNode.ConstraintType, ComboBoxModel>();
	try {
	    for (SearchDataNode.ConstraintType type : ConstraintType.values()) {
		final Field modelHolder = SearchDataNodeConstraintCellEditor.class.getDeclaredField(type.name().toLowerCase() + "Model");
		final ComboBoxModel model = (ComboBoxModel) modelHolder.get(this);
		map.put(type, model);
	    }
	} catch (final Exception x) {
	    logger.error(SearchDataNodeConstraintCellEditor.class.getSimpleName()+"-1", x);
	}
	dynamicEditorModel = Collections.unmodifiableMap(map);
	;
    }

    @Override
    public Object getCellEditorValue() {
	//TODO not working ? requires callback on TreeModel containing SearchData ?!
	return editorComboBox.getSelectedItem();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
	final ConstraintType constraintType = (ConstraintType) table.getValueAt(row, column - 1);
	final ComboBoxModel dynamicComboBoxModel = dynamicEditorModel.get(constraintType);
	if (dynamicComboBoxModel != null) {
	    editorComboBox.setModel(dynamicComboBoxModel);
	} else {
	    editorComboBox.setModel(defaultModel);
	}
	editorComboBox.setSelectedItem(value);
	return editorComboBox;
    }

}
