package org.rosuda.visualizer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

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
	
	@SuppressWarnings("unchecked")
	public Collection<Node<T>> getSelectedNodes() {
		final Collection<Node<T>> selection = new ArrayList<Node<T>>();
		final TreePath[] selectedPaths = tree.getSelectionPaths();
		if (selectedPaths == null)
			return Collections.unmodifiableCollection(selection);
		for (TreePath selectedPath : selectedPaths) {
			selection.add((Node<T>) ((NodeTreeModel.NodeToTreeNodeWrapper<T>)selectedPath.getLastPathComponent()).getNode());
		}
		return Collections.unmodifiableCollection(selection);
	}
	
}
