package org.rosuda.ui.dialog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.rosuda.type.Node;

import com.google.common.collect.Iterables;

public class RootlessIterable<T> implements Iterable<Node<T>> {

    final List<Node<T>> childIterators = new ArrayList<Node<T>>();

    public RootlessIterable(Iterable<Node<T>> data) {
	for (final Node<T> node : data) {
	    if (node.getParent() == null) {
		Iterables.addAll(childIterators, node.getChildren());
	    } else {
		childIterators.add(node);
	    }
	}
    }

    @Override
    public Iterator<Node<T>> iterator() {
	return childIterators.iterator();
    }

}
