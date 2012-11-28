package org.rosuda.ui.search;

import java.awt.Component;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.rosuda.ui.search.SearchDataNode.ConstraintType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TypesafeTableCellEditor extends AbstractTableCellEditor {

    private static final Logger logger = LoggerFactory.getLogger(TypesafeTableCellEditor.class);
    private final JTextField stringInput;
    private final JCheckBox booleanInput;
    private final JTextField numberInput;
    private final JTextField nameInput;
    private final Map<ConstraintType, JComponent> dynamicEditor;
    private ConstraintType constraintType;

    TypesafeTableCellEditor() {
	this.stringInput = new JTextField(30);
	this.booleanInput = new JCheckBox();
	this.numberInput = new JTextField();
	this.nameInput = new JTextField();
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
	    setTextfieldValue(stringInput, value);
	    break;
	case Number: 
	    setTextfieldValue(numberInput, value);
	    break;
	case Name:
	    setTextfieldValue(nameInput, table.getValueAt(row, 0));
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
	    final String inputStringValue = numberInput.getText().trim();
	    try {
		return new BigDecimal(inputStringValue);
	    } catch (final NumberFormatException numf) {
		logger.error(TypesafeTableCellEditor.class.getSimpleName() + "-"+NumberFormatException.class.getSimpleName() +" : \"" + inputStringValue+"\"");
		super.cancelCellEditing();
	    }
	case Name : 
	    return nameInput.getText();
	default:
	    return null;
	}
    }

    // --helper
    private void setTextfieldValue(final JTextField input, final Object value) {
	if (value != null) {
	    if (value instanceof String) {
		input.setText((String)value);
	    } else {
		input.setText(value.toString());
	    }
	    
	} else {
	    input.setText("");
	}
    }

}
