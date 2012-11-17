package org.rosuda.ui.search;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;

public abstract class AbstractTableCellEditor implements TableCellEditor {

    private final List<CellEditorListener> listeners = new ArrayList<CellEditorListener>();

    @Override
    public final boolean isCellEditable(EventObject anEvent) {
	return true;
    }

    @Override
    public final boolean shouldSelectCell(EventObject anEvent) {
	return true;
    }

    @Override
    public final boolean stopCellEditing() {
	ChangeEvent event = new ChangeEvent(this);
	for (CellEditorListener listener : listeners.toArray(new CellEditorListener[listeners.size()])) {
	    listener.editingStopped(event);
	}
	return true;
    }

    @Override
    public final void cancelCellEditing() {
	ChangeEvent event = new ChangeEvent(this);
	for (CellEditorListener listener : listeners.toArray(new CellEditorListener[listeners.size()])) {
	    listener.editingCanceled(event);
	}
    }

    @Override
    public final void addCellEditorListener(CellEditorListener l) {
	listeners.add(l);
    }

    @Override
    public final void removeCellEditorListener(CellEditorListener l) {
	listeners.remove(l);
    }

}
