package org.rosuda.ui.search;

import java.awt.Component;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.rosuda.ui.search.SearchDataNode.ConstraintType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TypesafeTableCellEditor extends AbstractTableCellEditor {

    private static final Logger logger = LoggerFactory.getLogger(TypesafeTableCellEditor.class);
    @SuppressWarnings("unused")
    private final JTextField stringInput;
    @SuppressWarnings("unused")
    private final JCheckBox booleanInput;
    @SuppressWarnings("unused")
    private final JTextField numberInput;
    @SuppressWarnings("unused")
    private final JLabel nameInput;
    private final Map<ConstraintType, JComponent> dynamicEditor;
    private ConstraintType constraintType;

    TypesafeTableCellEditor() {
	this.stringInput = new JTextField(30);
	this.booleanInput = new JCheckBox();
	this.numberInput = new JTextField();
	this.nameInput = new JLabel();
	final Map<ConstraintType, JComponent> map = new HashMap<SearchDataNode.ConstraintType, JComponent>();
	try {
	    for (SearchDataNode.ConstraintType type : ConstraintType.values()) {
		final Field modelHolder = TypesafeTableCellEditor.class.getDeclaredField(type.name().toLowerCase() + "Input");
		final JComponent model = (JComponent) modelHolder.get(this);
		map.put(type, model);
	    }
	} catch (final Exception x) {
	    logger.error(SearchDataNodeConstraintCellEditor.class.getSimpleName() + "-1", x);
	}
	dynamicEditor = Collections.unmodifiableMap(map);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
	constraintType = (ConstraintType) table.getValueAt(row, column - 2);
	switch (constraintType) {
	case Boolean:
	    booleanInput.setSelected((Boolean) value);
	    break;
	case String:
	    stringInput.setText((String) value);
	    break;
	case Number:
	    if (value != null) {
		numberInput.setText(value.toString());
	    } else {
		numberInput.setText("");
	    }
	    break;
	default:
	    return null;
	}
	return dynamicEditor.get(constraintType);
    }

    @Override
    public Object getCellEditorValue() {
	switch (constraintType) {
	case Boolean:
	    return booleanInput.isSelected();
	case String:
	    return stringInput.getText();
	case Number:
	    try {
		return new BigDecimal(numberInput.getText().trim());
	    } catch (final NumberFormatException numf) {
		logger.error(TypesafeTableCellEditor.class.getSimpleName() + "-1", numf);
	    }
	default:
	    return null;
	}
    }

}
