package org.rosuda.type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TreeUtil {

	public static final String NODE_SEPARATOR = "/";
	
	//TODO: id is currently determined by nodeName:String[#child:int]/ ..
	//how about an indepentent matching schema
	//node:  name,int value class ?
	//value: type, value
	
	public static String getId(final Node<?> node, final Value value) {
		final StringBuilder builder = new StringBuilder();
		builder.append(getId(node));
		builder.append(getId(value));
		return builder.toString();
	}
	
	public static String getId(final Value value) {
		final StringBuilder builder = new StringBuilder();
		builder.append("[@type='").append(value.getType().name());
		builder.append("' and @value='");
		switch (value.getType()) {
		case BOOL: builder.append(value.getBool()); break;
		case NUMBER: builder.append(value.getNumber()); break;
		case STRING: builder.append(value.getString()); break;
		default: builder.append(value.toString()); break;
		}
		builder.append("']");
		return builder.toString();
	}
	
	public static String getId(final Node<?> node) {
		if (node == null)
			return null;
		final List<Node<?>> nodePath = new ArrayList<Node<?>>();
		Node<?> parent = node;
		while (parent != null) {
			nodePath.add(parent);
			parent = parent.getParent();
		}
		Collections.reverse(nodePath);
		final StringBuilder builder = new StringBuilder();
		for (int i=0; i < nodePath.size(); i++) {
			//check nodeNo with name
			final Node<?> currentNode = nodePath.get(i);
			builder.append(currentNode.getName());
			if (i>0) {
				parent = nodePath.get(i-1);
				if (parent.getChildCount() > 0) {
					int idx = 0;
					for (int p=0; p<parent.getChildCount();p++) {
						final Node<?> pthChild = parent.childAt(p);
						if (pthChild.equals(currentNode)) {
							p = parent.getChildCount();
							break;
						}
						if (currentNode.getName().equals(pthChild.getName())) {
							idx++;
						}
					}
					builder.append("[").append(idx).append("]");
				}
			}
			//TODO possibly add [x] if more children are present
			if (i<nodePath.size() -1)
				builder.append(NODE_SEPARATOR);
		}
		return builder.toString();
	}
	
	public static String getId(final Node.Builder<?> node) {
		if (node == null)
			return null;
		final List<Node.Builder<?>> nodePath = new ArrayList<Node.Builder<?>>();
		Node.Builder<?> parent = node;
		while (parent != null) {
			nodePath.add(parent);
			parent = parent.getParent();
		}
		Collections.reverse(nodePath);
		final StringBuilder builder = new StringBuilder();
		for (int i=0; i < nodePath.size(); i++) {
			//check nodeNo with name
			final Node.Builder<?> currentNode = nodePath.get(i);
			builder.append(currentNode.getName());
			if (i>0) {
				parent = nodePath.get(i-1);
				if (parent.getChildCount() > 0) {
					int idx = 0;
					for (int p=0; p<parent.getChildCount();p++) {
						final Node.Builder<?> pthChild = parent.childAt(p);
						if (pthChild.equals(currentNode)) {
							p = parent.getChildCount();
							break;
						}
						if (currentNode.getName().equals(pthChild.getName())) {
							idx++;
						}
					}
					builder.append("[").append(idx).append("]");
				}
			}
			//TODO possibly add [x] if more children are present
			if (i<nodePath.size() -1)
				builder.append(NODE_SEPARATOR);
		}
		return builder.toString();
	}
		
//	@SuppressWarnings({ "unchecked", "rawtypes" })
//	public static final Node<?> buildGeneric(final Node.Builder<?> node) {
//		final Node.Builder<?> proxy = NodeProxy.createProxy(node);
//		if (node.getChildCount() > 0) {
//			//replace child by proxy of child!
//			final List<Node.Builder<?>> children = new ArrayList<Node.Builder<?>>();
//			for (final Node.Builder<?> nodeChild : node.getChildren()) {
//				if (nodeChild instanceof Node.Builder<?>) 
//					children.add((Node.Builder<?>) nodeChild);
//			}
//			//remove current children
//			for (final Node.Builder<?> removeChild : children) {
//				node.remove((Node.Builder) removeChild);
//			}
//			//add proxies
//			for (final Node.Builder<?> addChild : children) {
//				node.add((Node.Builder)buildGeneric(addChild));
//			}
//		}
//		return proxy;
//	}
}
