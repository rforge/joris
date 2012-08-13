package org.rosuda.ui.mmi;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTree;
import org.rosuda.irconnect.IREXP;
import org.rosuda.ui.SwingLayoutProcessor;
import org.rosuda.ui.context.UIContext;
import org.rosuda.ui.core.mvc.HasValue;
import org.rosuda.ui.core.mvc.HasValue.ValueChangeListener;
import org.rosuda.ui.core.mvc.impl.PropertyHasValueImpl;
import org.rosuda.visualizer.NodeTreeModel;

public class MMIToolViewDialogImpl extends JDialog implements MMIToolView<JDialog> {

    public JXTree multiselector;
    public JXTable valuetable;
    private UIContext uiContext;

    public MMIToolViewDialogImpl(UIContext context) throws Exception {
	super(context.getUIFrame(), ModalityType.MODELESS);
	this.uiContext = context;
	System.out.println("rendering ..");
	SwingLayoutProcessor.processLayout(this, "/gui/dialog/MMISpreadSheetDialog.xml");
	System.out.println(">>> multiselector = "+multiselector);
    }

    @Override
    public JDialog getContainer() {
	return this;
    }

    @Override
    public HasValue<NodeTreeModel<IREXP>> getUniqueStructureTree() {
	System.out.println(">>> multiselector = "+multiselector);
	return new HasValue<NodeTreeModel<IREXP>>() {

	    final List<HasValue.ValueChangeListener<NodeTreeModel<IREXP>>> listeners = new ArrayList<HasValue.ValueChangeListener<NodeTreeModel<IREXP>>>();
	    @Override
	    public NodeTreeModel<IREXP> getValue() {
		return (NodeTreeModel<IREXP>) multiselector.getModel();
	    }

	    @Override
	    public void setValue(NodeTreeModel<IREXP> value) {
		final boolean valueChanged = (value != multiselector.getModel()); 
		multiselector.setModel(value);
		if (valueChanged) {
		    fireChangeEvent(value);
		}
	    }
	    
	    private void fireChangeEvent(final NodeTreeModel<IREXP> newValue) {
		for (final HasValue.ValueChangeListener<NodeTreeModel<IREXP>> listener : new ArrayList<HasValue.ValueChangeListener<NodeTreeModel<IREXP>>>(listeners)) {
		    listener.onValueChange(newValue);
		}
	    }

	    @Override
	    public void addChangeListener(org.rosuda.ui.core.mvc.HasValue.ValueChangeListener<NodeTreeModel<IREXP>> listener) {
		listeners.add(listener);
	    }

	    @Override
	    public void removeChangeListener(org.rosuda.ui.core.mvc.HasValue.ValueChangeListener<NodeTreeModel<IREXP>> listener) {
		listeners.remove(listener);
	    }
	    
	};
    }
}
