package org.rosuda.util.nodelistcalc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.rosuda.type.Node;
import org.rosuda.type.NodeFinder;
import org.rosuda.type.NodeFinderImpl;
import org.rosuda.type.NodePath;
import org.rosuda.type.Value;
import org.rosuda.type.Value.Type;
import org.rosuda.util.calculator.Calculator;
import org.rosuda.util.calculator.interpreter.Interpreter;
import org.rosuda.util.calculator.parser.TokenizerException;
import org.rosuda.util.calculator.parser.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListCalculationUtil<T> {

    public static final String ASSIGNMENT_OPERATOR = ":=";
    private static final Logger LOGGER = LoggerFactory.getLogger(ListCalculationUtil.class);
    private NodeFinder<T> finder = new NodeFinderImpl<T>();
    private final List<Node<T>> nodeList = new ArrayList<Node<T>>();

    private final Map<String, List<Number>> asMap = new HashMap<String, List<Number>>();

    public final void setContent(final List<Node<T>> newContent) {
	this.nodeList.clear();
	if (newContent != null)
	    this.nodeList.addAll(newContent);
    }

    public List<Number> calculate(final String string) {
	final int assignmentIdx = string.indexOf(ASSIGNMENT_OPERATOR);
	if (assignmentIdx > 0) {
	    final String as = string.substring(0, assignmentIdx).replaceAll("@", "").trim();
	    final String eval = string.substring(ASSIGNMENT_OPERATOR.length() + assignmentIdx).trim();
	    return calculateAs(eval, as);
	}
	return calculateAs(string, null);
    }

    /**
     * stores the result for fast access as "as"
     * 
     * @param string
     * @param as
     * @return
     */
    public List<Number> calculateAs(final String string, final String as) {
	final List<Number> props = new ArrayList<Number>();
	if (nodeList == null)
	    return props;
	try {
	    final TreeNode ast = createSolverForString(string);
	    if (ast == null)
		return props;
	    for (int i = 0; i < nodeList.size(); i++) {
		props.add(getValueForCustomModel(ast, i));
	    }
	} catch (final TokenizerException tko) {
	    for (int i = 0; i < nodeList.size(); i++) {
		if (string.startsWith("@")) {
		    final String pathId = string.substring(1);
		    final List<Number> cachedResult = asMap.get(pathId);
		    if (cachedResult != null) {
			props.add(cachedResult.get(i));
		    } else {
			LOGGER.warn("undefined attribute @" + pathId);
		    }
		} else {
		    props.add(getNodeValue(string, i));
		}
	    }
	}
	if (as != null) {
	    this.asMap.put(as, props);
	}
	return props;
    }

    private TreeNode createSolverForString(final String formula) {
	final TreeNode expressionNode = Calculator.createTree(formula);
	if (expressionNode != null) {
	    final Number evalulationTest = Calculator.evaluate(expressionNode, new Interpreter.CombiReferenceResolver() {
		int count = 0;

		public boolean next() {
		    return (++count) < 5;
		}

		public Number resolveRef(final String id) {
		    return 1.0;
		}

		public Number resolveLoopRef(String id) {
		    return 1.0;
		}
	    });
	    if (evalulationTest == null) {
		return null;
	    }
	}
	return expressionNode;
    }

    private Number getValueForCustomModel(final TreeNode formula, final int idx) {
	final Interpreter.CombiReferenceResolver combiRefResolver = new Interpreter.CombiReferenceResolver() {
	    final Iterator<Node<T>> modelIter = nodeList.iterator();

	    int index = 0;

	    public boolean next() {
		return modelIter.hasNext();
	    }

	    public Number resolveRef(final String id) {
		final Node<T> object = nodeList.get(idx);
		if (id.startsWith("@")) {
		    final String pathId = id.substring(1);
		    final List<Number> cachedResult = asMap.get(pathId);
		    if (cachedResult != null)
			return cachedResult.get(idx);
		    else {
			LOGGER.warn("undefined reference @" + pathId);
			return null;
		    }
		}
		return getNodeValue(object, id);
	    }

	    public Number resolveLoopRef(final String id) {
		final Node<T> object = modelIter.next();
		if (id.startsWith("@")) {
		    if (idx >= ListCalculationUtil.this.nodeList.size())
			return null;
		    // use id from metaBeans:
		    final String pathId = id.substring(1);
		    final List<Number> cachedResult = asMap.get(pathId);
		    if (cachedResult != null)
			return cachedResult.get(index++);
		    else {
			LOGGER.warn("undefined reference @" + pathId);
			return null;
		    }
		}
		return getNodeValue(object, id);
	    }
	};
	final Number value = Calculator.evaluate(formula, combiRefResolver);
	return value;
    }

    private Number getNodeValue(final String nodePath, final int i) {
	final Node<T> object = nodeList.get(i);
	return getNodeValue(object, nodePath);
    }

    private Number getNodeValue(final Node<T> object, final String nodePath) {
	final NodePath path = NodePath.Impl.parse(nodePath);
	return getNodeValue(object, path);
    }

    private Number getNodeValue(final Node<T> root, final NodePath path) {
	final Node<T> numberValueNode = finder.findNode(root, path);
	if (numberValueNode == null) {
	    return null;
	} else if (numberValueNode.getValue() != null) {
	    final Value nodeValue = numberValueNode.getValue();
	    if (Type.NUMBER.equals(nodeValue.getType())) {
		return nodeValue.getNumber();
	    } else {
		return null;
	    }
	} else {
	    return null;
	}
    }
    
    public void setNodeFinder(final NodeFinder<T> nodeFinder) {
	this.finder = nodeFinder;
    }
}
