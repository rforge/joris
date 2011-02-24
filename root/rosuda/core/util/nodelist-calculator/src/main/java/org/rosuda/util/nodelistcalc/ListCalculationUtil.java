package org.rosuda.util.nodelistcalc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

public class ListCalculationUtil {

	private static final Log log = LogFactory.getLog(ListCalculationUtil.class);
	@SuppressWarnings("rawtypes")
	private final NodeFinder finder = new NodeFinderImpl();
	private final List<Node<?>> nodeList = new ArrayList<Node<?>>();

	private final Map<String, List<Number>> asMap = new HashMap<String, List<Number>>();
	
	public final void setContent(final List<Node<?>> newContent) {
		this.nodeList.clear();
		if (newContent != null)
			this.nodeList.addAll(newContent);
	}

	public List<Number> calculate(final String string) {
		return calculateAs(string, null);
	}
	
	/**
	 * stores the result for fast access as "as"
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
						log.warn("undefined attribute @"+pathId);
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
			final Number evalulationTest = Calculator.evaluate(expressionNode,
					new Interpreter.CombiReferenceResolver() {
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
			final Iterator<Node<?>> modelIter = nodeList.iterator();

			int index = 0;

			public boolean next() {
				return modelIter.hasNext();
			}

			public Number resolveRef(final String id) {
				final Node<?> object = nodeList.get(idx);
				if (id.startsWith("@")) {
					final String pathId = id.substring(1);
					final List<Number> cachedResult = asMap.get(pathId);
					if (cachedResult != null)
						return cachedResult.get(idx);
					else {
						log.warn("undefined reference @"+pathId);
						return null;
					}
				}
				return getNodeValue(object, id);
			}

			public Number resolveLoopRef(final String id) {
				final Node<?> object = modelIter.next();
				if (id.startsWith("@")) {
					if (idx >= ListCalculationUtil.this.nodeList.size())
						return null;
					// use id from metaBeans:
					final String pathId = id.substring(1);
					final List<Number> cachedResult = asMap.get(pathId);
					if (cachedResult != null)
						return cachedResult.get(index++);
					else {
						log.warn("undefined reference @"+pathId);
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
		final Node<?> object = nodeList.get(i);
		return getNodeValue(object, nodePath);
	}

	private Number getNodeValue(final Node<?> object, final String nodePath) {
		final NodePath path = NodePath.Impl.parse("/" + Node.ROOTNAME + "/"
				+ nodePath);
		return getNodeValue(object, path);
	}

	private Number getNodeValue(final Node<?> root, final NodePath path) {
		@SuppressWarnings("unchecked")
		final Node<?> numberValueNode = finder.findNode(root, path);
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
}
