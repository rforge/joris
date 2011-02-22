package org.rosuda.visualizer;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionListener;

import org.rosuda.type.Node;

public class Visualizer<T> extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8644418267184074482L;

	private final JTree tree;
	
	public Visualizer(final Node<T> node) {
		this.tree = new JTree();
		tree.setCellRenderer(new DefaultNodeRenderer());
		tree.setModel(new NodeTreeModel<T>(node));
		tree.setDragEnabled(false);
		super.add(tree);
	}
	
	public void addTreeSelectionListener(final TreeSelectionListener listener) {
		tree.addTreeSelectionListener(listener);
	}

	public void removeAllTreeSelectionListeners() {
		for (final TreeSelectionListener tsl: tree.getTreeSelectionListeners()) {
			tree.removeTreeSelectionListener(tsl);
		}
	}
	
	public void enableDragMode() {
		tree.setDragEnabled(true);
	}
	
}
