package org.rosuda.visualizer;

import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import org.rosuda.type.Node;
import org.rosuda.type.Value;

public class NodeTreeModel<T> extends DefaultTreeModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5766287947399820361L;

	static class ValueToTreeNodeWrapper<T> implements TreeNode {

		private final TreeNode parent;
		private final Value value;
		
		public ValueToTreeNodeWrapper(final Value value, final TreeNode parent) {
			this.value = value;
			this.parent = parent;
		}

		public Enumeration<TreeNode> children() {
			return new Enumeration<TreeNode>() {
				int i = 0;
				
				public boolean hasMoreElements() {
					return i < getChildCount();
				}

				public TreeNode nextElement() {
					return getChildAt(i++);
				}
			};
		}

		public boolean getAllowsChildren() {
			return true;
		}

		public TreeNode getChildAt(final int idx) {
			// TODO Auto-generated method stub
			return null;
		}

		public int getChildCount() {

			return value != null ? 1 : 0;
		}

		public int getIndex(final TreeNode node) {
			// TODO Auto-generated method stub
			return 0;
		}

		public TreeNode getParent() {
			return parent;
		}

		public boolean isLeaf() {
			return true;
		}
		
		@Override
		public String toString() {
			if (value != null) {
				switch (value.getType()) {
				case BOOL: return Boolean.toString(value.getBool());
				case NUMBER: return value.getNumber().toString();
				case STRING: return value.getString();
				case REFERENCE: return new StringBuilder().append("<ReferenceTo>").append(value.getString()).append("</ReferenceTo>").toString();
				}
			}
			return null;
		}

		public Value getValue() {
			return value;
		}
		
	}
	static class NodeToTreeNodeWrapper<T> implements TreeNode {

		private final Node<T> node;
		private final Map<Integer, TreeNode> lazyChildren = new TreeMap<Integer, TreeNode>();
		private final TreeNode parent;
		final boolean hasValue;
		final int childCount;
		
		NodeToTreeNodeWrapper(final Node<T> node) {
			this(node, null);
		}
		
		private NodeToTreeNodeWrapper(final Node<T> node, final TreeNode parent) {
				this.node = node;
				this.parent = parent;
				hasValue = node.getValue() != null;
				childCount = node.getChildCount();
		}
		
		public int getChildCount() {
			if (childCount > 0)
				return childCount;
			else if (hasValue) 
				return 1;
			else 
				return 0;
		}
		
		public TreeNode getChildAt(final int index) {
			if (!lazyChildren.containsKey(index)) {
				if (childCount > 0 && index < node.getChildCount()) {
					final NodeToTreeNodeWrapper<T> lazyChild = new NodeToTreeNodeWrapper<T>(this.node.childAt(index), this);
					lazyChildren.put(index, lazyChild);
					return lazyChild;
				} else if (hasValue) {
					final ValueToTreeNodeWrapper<T> lazyChild = new ValueToTreeNodeWrapper<T>(this.node.getValue(), this);
					lazyChildren.put(index, lazyChild);
					return lazyChild;
				} else throw new IllegalArgumentException("index "+index+" is out of bounds");
			}
			return lazyChildren.get(index);
		}

		public TreeNode getParent() {
			return parent;
		}

		public int getIndex(final TreeNode node) {
			System.out.println("getIdx called on "+node);
			if (lazyChildren.containsValue(node)) {
				//TODO
			}
			return -1;
		}

		public boolean getAllowsChildren() {
			return !isLeaf();
		}

		public boolean isLeaf() {
			return node.getChildCount() == 0 && node.getValue() == null;
		}

		public Enumeration<TreeNode> children() {
			return new Enumeration<TreeNode>() {
				int i = 0;
				
				public boolean hasMoreElements() {
					return i < node.getChildCount();
				}

				public TreeNode nextElement() {
					return getChildAt(i++);
				}
			};
		}
		
		@Override
		public String toString() {
			return node.getName();
		}
		
		public Node<?> getNode() {
			return node;
		}
	}
	
	public NodeTreeModel(final Node<T> node) {
		super(new NodeToTreeNodeWrapper<T>(node));
	}
}
