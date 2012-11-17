package org.rosuda.ui.search;

import javax.swing.DefaultComboBoxModel;

import org.rosuda.ui.search.SearchDataNode.ConstraintType;

public class ConstraintListModel extends DefaultComboBoxModel {

    /**
     * 
     */
    private static final long serialVersionUID = 1974516569738561825L;

    @Override
    public int getSize() {
	return ConstraintType.values().length;
    }

    @Override
    public Object getElementAt(int index) {
	return ConstraintType.values()[index];
    }

}
