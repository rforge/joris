package org.rosuda.ui.mmi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.rosuda.type.Node;
import org.rosuda.type.NodeFinder;
import org.rosuda.type.NodePath;
import org.rosuda.type.PostfixNodeFinderImpl;
import org.rosuda.type.Value;
import org.rosuda.util.nodelistcalc.ListCalculationUtil;
import org.rosuda.visualizer.NodeTreeSelection;
import org.slf4j.LoggerFactory;

public class MMIDynamicTableModel<T> extends AbstractTableModel {

    /**
     * 
     */
    private static final long serialVersionUID = -3856235071311313837L;
    private final List<Node<T>> nodes;
    private NodeTreeSelection currentSelection;
    private final ListCalculationUtil<T> calculationUtil;
    private final NodeFinder<T> nodeFinder = new PostfixNodeFinderImpl<T>();

    public MMIDynamicTableModel(final Collection<Node<T>> data) {
	this.nodes = new ArrayList<Node<T>>(data);
	this.calculationUtil = new ListCalculationUtil<T>();
	this.calculationUtil.setContent(nodes);
	this.currentSelection = null;
	//TODO UI TESTnew NodeTreeSelection.Impl(Arrays.asList(NodePath.Impl.parse("/"), NodePath.Impl.parse("call/rsymbol[0]"), NodePath.Impl.parse("df/Integer[0]")));
    }

    @Override
    public int getRowCount() {
	return nodes.size();
    }

    @Override
    public int getColumnCount() {
	if (currentSelection == null) {
	    return 0;
	}
	return currentSelection.getSelectedPaths().size();
    }

    @Override
    public String getColumnName(final int column) {
	final NodePath valueSelector = currentSelection.getSelectedPaths().get(column);
	return (valueSelector == null ? "?" : valueSelector.toString());
    }

    @Override
    public Object getValueAt(int row, int column) {
	final NodePath valueSelector = currentSelection.getSelectedPaths().get(column);//replace "//" -> "/"
	if (valueSelector == null)
	    return null;
	final Node<T> root = nodes.get(row);
	final String expression = toExpression(valueSelector);
	if (expression != null) {
	    return calculationUtil.calculate(expression).get(row);
	}
	final Node<T> value = nodeFinder.findNode(root, valueSelector);

	if (value == null) {
	    return "-null-("+valueSelector+")";
	}
	final Value v = value.getChildCount() == 1 ? value.childAt(0).getValue() : value.getValue();
	if (v != null) {
	    switch (v.getType()) {
	    case BOOL:
		return v.getBool();
	    case STRING:
		return v.getString();
	    case NUMBER:
		return v.getNumber();
	    default:
		return v;
	    }
	} else {
	    return value.getName() + "/[0.." + value.getChildCount() + "]";
	}

	// try {
	// final String evalExpr = "${"+valueSelector.toString()+"}";
	// System.out.println("**getValueAt("+row+","+column+"), expr ="+evalExpr+" valueSelector="+valueSelector.toString());
	// return calculationUtil.calculate(evalExpr).get(row);
	// } catch (final Exception x) {
	// x.printStackTrace();
	// return x.getMessage();
	//
	// }
    }

    private String toExpression(final NodePath valueSelector) {
	final String expr = valueSelector.getId().getName();
	if (expr.contains("@") || expr.contains("$")) {
	    return expr;
	}
	return null;
    }

    public void updateSelection(NodeTreeSelection newValue) {
	// threaded-overtaking-queue (last one wins, forget in between)
	this.currentSelection = newValue;
	super.fireTableStructureChanged();
    }

}
