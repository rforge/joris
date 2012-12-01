package org.rosuda.mvc.swing.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class SelectionListModelAdapter<T> implements ListSelectionModel{

    /**
     * 
     */
    private static final long serialVersionUID = 1414609451596672612L;
    private final List<ListSelectionListener> listSelectionListeners;
    private static final int NOT_SELECTED_INDEX = 0;
    private final List<T> valueList;
    private final Set<Integer> selectedIndices;
    private int selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;
    private int anchorSelectionIndex = NOT_SELECTED_INDEX;
    private int leadSelectionIndex = NOT_SELECTED_INDEX;
    private boolean valueIsAdjusting =false;
    
    public SelectionListModelAdapter() {
	this.valueList = new ArrayList<T>();
	selectedIndices = new TreeSet<Integer>();
	listSelectionListeners = new ArrayList<ListSelectionListener>();
    }
    
    public void updateValues(final List<T> newValues) {
	valueList.clear();
	if (newValues != null) {
	    valueList.addAll(newValues);
	}
    }
    
    public void addValue(final T value) {
	valueList.add(value);
    }

    public List<T> getValues() {
 	return Collections.unmodifiableList(valueList);
     }
    
    @Override
    public void setSelectionInterval(final int from, final int to) {
	selectedIndices.clear();
	addSelectionInterval(from, to);
    }

    @Override
    public void addSelectionInterval(final int from, final int to) {
	for (int i=from; i<=to;i++) {
	    selectedIndices.add(i);
	}
	fireSelectionModelChange(from, to);
    }

    @Override
    public void removeSelectionInterval(final int from, final int to) {
	for (int i=from; i<=to;i++) {
	    selectedIndices.remove(i);
	}
	fireSelectionModelChange(from, to);
    }

    @Override
    public int getMinSelectionIndex() {
	if (selectedIndices.isEmpty()) {
	    return NOT_SELECTED_INDEX;
	}
	return selectedIndices.iterator().next();
    }

    @Override
    public int getMaxSelectionIndex() {
	if (selectedIndices.isEmpty()) {
	    return NOT_SELECTED_INDEX;
	}
	return selectedIndices.toArray(new Integer[selectedIndices.size()])[selectedIndices.size()-1];
    }

    @Override
    public boolean isSelectedIndex(final int index) {
	return (selectedIndices.contains(index));
    }

    @Override
    public int getAnchorSelectionIndex() {
	return anchorSelectionIndex;
    }

    @Override
    public void setAnchorSelectionIndex(final int index) {
	this.anchorSelectionIndex = index;
    }

    @Override
    public int getLeadSelectionIndex() {
	return leadSelectionIndex;
    }

    @Override
    public void setLeadSelectionIndex(final int index) {
	this.leadSelectionIndex = index;
    }

    @Override
    public void clearSelection() {
	selectedIndices.clear();
    }

    @Override
    public boolean isSelectionEmpty() {
	return selectedIndices.isEmpty();
    }

    @Override
    public void insertIndexInterval(int index, int length, boolean before) {
	// TODO Auto-generated method stub
	
    }

    @Override
    public void removeIndexInterval(int from, int to) {
	removeSelectionInterval(from, to);
	
    }

    @Override
    public void setValueIsAdjusting(boolean valueIsAdjusting) {
	this.valueIsAdjusting = valueIsAdjusting;
    }

    @Override
    public boolean getValueIsAdjusting() {
	return valueIsAdjusting;
    }

    @Override
    public void setSelectionMode(final int selectionMode) {
	this.selectionMode = selectionMode;
    }

    @Override
    public int getSelectionMode() {
	return selectionMode;
    }

    @Override
    public void addListSelectionListener(final ListSelectionListener listener) {
	this.listSelectionListeners.add(listener);
	
    }

    @Override
    public void removeListSelectionListener(final ListSelectionListener listener) {
	this.listSelectionListeners.remove(listener);
    }

    private void fireSelectionModelChange(int firstIndex, int lastIndex) {
	for (final ListSelectionListener listener: new ArrayList<ListSelectionListener>(listSelectionListeners)) { 
	    listener.valueChanged(new ListSelectionEvent(this, firstIndex, lastIndex, valueIsAdjusting));
	}
    }

}
