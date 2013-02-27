package org.rosuda.util.calculator.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TreeNode {

	//for parser
	public enum Type {
		ADD,SUB,MUL,DIV,POW, 
		FUNCTION, VALUE, REFERENCE, BRACKET, SEPARATOR	 
	}
	
	static final Set<Type> pointType = new HashSet<Type>();
	static {
		pointType.add(Type.MUL);
		pointType.add(Type.DIV);
		pointType.add(Type.POW);
	}

	static final Set<Type> lineType = new HashSet<Type>();
	static {
		lineType.add(Type.ADD);
		lineType.add(Type.SUB);	
	}
	
	static Comparator<Type> typeComparator = new Comparator<Type>(){
		public int compare(final Type o1,final  Type o2) {
			if (lineType.contains(o1)&&lineType.equals(o2)) {
				return 0;
			} else if (pointType.contains(o1)&&pointType.contains(o2)) {
				return 0;
			} else if (pointType.contains(o1)&&lineType.contains(o2)) {
				return -1;
			} else if (pointType.contains(o2)&&lineType.contains(o1)) {
				return 1;
			}
			return 0;
		}
		
	};
	
	private final Type type;
	final List<TreeNode> children = new ArrayList<TreeNode>();
	private Object value;

	private TreeNode(final Type type) {
		this.type = type;
	}

	public final Type getType() {
		return type;
	}

	public final List<TreeNode> getChildren() {
		return Collections.unmodifiableList(children);
	}
	
	public void addChild(final TreeNode child) {
		if (child==null)
			throw new IllegalArgumentException("null child not allowed!");
		children.add(child);
	}
	
	void removeChild(final TreeNode child) {
		children.remove(child);
	}
	
	public boolean isLeaf() {
		return value != null;
	}
	
	public Object getValue() {
		return value;
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (obj==null||(!(obj instanceof TreeNode)))
			return false;
		final TreeNode node = (TreeNode) obj;
		if (!node.type.equals(type))
			return false;
		if (value==null&&node.value!=null||value!=null&&node.value==null)
			return false;
		if (value!=null&&node.value!=null&&!value.equals(node.value))
			return false;
		//check children
		if (node.children.size()!=children.size())
			return false;
		boolean same = true;
		for (int i = 0 ; i < children.size() ; i++) {
			boolean childEquals = children.get(i).equals(node.getChildren().get(i));
			same = same && childEquals;
		}
		return same;
	}
	
	@Override
	public String toString() {
		return new StringBuffer()
			.append("TreeNode[type=\"")
			.append(type)
			.append("\",value=\"")
			.append(value)
			.append("\",children={")
			.append(children)
			.append("}]")
			.toString();
	}
	
	public static TreeNode create(final Type type, final Object value) {
		final TreeNode leaf = new TreeNode(type);
		leaf.value = value;
		return leaf;
	}
	
	public static TreeNode buildLeaf(final Token token) {
		if (Token.Type.Reference.equals(token.type)) {
			final TreeNode leaf = new TreeNode(Type.REFERENCE);
			leaf.value = token.symbol;
			return leaf;
		} else if (Token.Type.Number.equals(token.type)) {
			final TreeNode leaf = new TreeNode(Type.VALUE);
			leaf.value = token.value;
			return leaf;
		} else if (Token.Type.Function.equals(token.type)) {
			final TreeNode leaf = new TreeNode(Type.FUNCTION);
			leaf.value = token.symbol;
			return leaf;
		}
		return null;
	}	
	
	public static TreeNode buildStructure(final Type type) {
		return new TreeNode(type);	
	}	
}
