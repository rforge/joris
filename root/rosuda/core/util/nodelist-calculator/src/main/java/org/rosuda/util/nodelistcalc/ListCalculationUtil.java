package org.rosuda.util.nodelistcalc;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rosuda.type.Node;
import org.rosuda.type.NodeFinder;
import org.rosuda.type.NodeFinderImpl;
import org.rosuda.type.NodePath;
import org.rosuda.type.TreeUtil;
import org.rosuda.type.Value;
import org.rosuda.type.Value.Type;

public class ListCalculationUtil<T> {

	private static final Log log = LogFactory.getLog(ListCalculationUtil.class);
	private final NodeFinder<T> finder = new NodeFinderImpl<T>();
	private final List<Node<T>> nodeList = new ArrayList<Node<T>>();
	//TODO cache for listElem/nodeProperty
	
	public final void setContent(final List<Node<T>> newContent) {
		this.nodeList.clear();
		if (newContent != null)
			this.nodeList.addAll(newContent);
	}

	public List<Number> calculate(final String nodeProperty) {
		final List<Number> props = new ArrayList<Number>();
		if (nodeList == null)
			return props;
		
		final NodePath path = NodePath.Impl.parse("/"+Node.ROOTNAME+"/"+nodeProperty);

		//TODO use Calculator!
		for (int i=0; i < nodeList.size(); i++) {
			final Node<T> root = nodeList.get(i);
			//showNode(i, root);
			//TreeUtil.getId(value)
			final Node<T> numberValueNode = finder.findNode(root, path);
			if (numberValueNode == null) {
				props.add(null);
			} else if (numberValueNode.getValue() != null){
				final Value nodeValue = numberValueNode.getValue();
				if (Type.NUMBER.equals(nodeValue.getType())) {
					props.add(nodeValue.getNumber());
				} else {
					props.add(null);
				}
			} else {
				props.add(null);
			}
		}
		return props;
	}
	
	private void showNode(final int idx, final Node<?> node) {
		if (node == null)
			return;
		System.out.println(idx+" : "+ TreeUtil.getId(node));
		for (final Node<?> child: node.getChildren()) {
			showNode(idx, child);
		}
	}
	
}
