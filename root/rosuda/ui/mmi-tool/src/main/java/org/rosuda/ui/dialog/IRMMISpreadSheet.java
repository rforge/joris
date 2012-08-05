package org.rosuda.ui.dialog;

import java.awt.Dialog.ModalityType;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.tree.TreeModel;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTree;
import org.rosuda.irconnect.IREXP;
import org.rosuda.type.Node;
import org.rosuda.type.Value;
import org.rosuda.ui.MainFrame;
import org.rosuda.ui.SwingLayoutProcessor;
import org.rosuda.ui.context.UIContext;
import org.rosuda.visualizer.NodeTreeModel;
import org.swixml.SwingEngine;

public class IRMMISpreadSheet extends JDialog {

    private final List<Node<IREXP>> data = new ArrayList<Node<IREXP>>();
    private final List<String> paths = new ArrayList<String>();

    public JXTree multiselector;
    public JXTable valuetable;

    public IRMMISpreadSheet(final UIContext context, final Collection<Node<IREXP>> data) throws Exception {
	super(context.getUIFrame(), ModalityType.MODELESS);
	if (data.isEmpty()) {
	    // TODO show empty warning dialog
	} else {
	    this.data.addAll(data);
	    SwingLayoutProcessor.processLayout(this, "/gui/dialog/MMISpreadSheetDialog.xml");	    
	    multiselector.setModel(new NodeTreeModel<IREXP>(new RootNodeWrapper(null, data)));
	    setVisible(true);
	    // TODO present data
	    // create distinct node paths

	    // use ListCalculationUtil to show data

	    // create JTableModel

	    // create a list of all nodes
	}
    }
}
