package org.rosuda.ui.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;
import org.rosuda.graph.service.search.Relation;
import org.rosuda.graph.service.search.VertexConstraint;
import org.rosuda.ui.model.SearchDataNode.ConstraintType;
import org.rosuda.visualizer.Localized;

public class SearchTreeModel extends AbstractTreeTableModel implements
		Serializable {

	private static final long serialVersionUID = 6527804524956955313L;
	private static final Log LOGGER = LogFactory.getLog(SearchTreeModel.class);

	enum Columns {

		NAME(String.class), TYPE(ConstraintType.class), TYPEVALUE(Enum.class), VALUE(
				Object.class);

		private Class<?> columnClass;

		Columns(final Class<?> columnClass) {

		}
	}

	private SearchDataNode root;
	private Localized localized;

	public SearchTreeModel() {
		final ResourceBundle localization = ResourceBundle
				.getBundle(SearchTreeModel.class.getName());
		this.localized = new Localized.ResourceBundleImpl(localization);
		root = new SearchDataNode(localization.getString("root"),
				ConstraintType.Name);
		SearchDataNode coefficient = new SearchDataNode("coefficient",
				ConstraintType.Name);
		SearchDataNode distNode = new SearchDataNode("dist",
				ConstraintType.Name);
		coefficient.addChild(distNode);

		final MathContext precion2 = new MathContext(2, RoundingMode.HALF_UP);
		distNode.addChild(new SearchDataNode("estimate", ConstraintType.Number)
				.setTypeValue(Relation.GT).setNumber(BigDecimal.ZERO));
		distNode.addChild(new SearchDataNode("estimate", ConstraintType.Number)
				.setTypeValue(Relation.LT).setNumber(
						new BigDecimal(0.15, precion2)));

		distNode.addChild(new SearchDataNode("p-value", ConstraintType.Number)
				.setTypeValue(Relation.GT).setNumber(BigDecimal.ZERO));
		distNode.addChild(new SearchDataNode("p-value", ConstraintType.Number)
				.setTypeValue(Relation.LT).setNumber(
						new BigDecimal(0.15, precion2)));

		root.addChild(coefficient);
		super.root = root;
	}

	public Object getChild(Object parent, int index) {
		return ((SearchDataNode) parent).getChildren().get(index);
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		SearchDataNode treenode = (SearchDataNode) parent;
		for (int i = 0; i > treenode.getChildren().size(); i++) {
			if (treenode.getChildren().get(i) == child) {
				return i;
			}
		}
		return 0;
	}

	public int getChildCount(Object parent) {
		return ((SearchDataNode) parent).getChildren().size();
	}

	public int getColumnCount() {
		return Columns.values().length;
	}

	public String getColumnName(int column) {
		return localized.get(Columns.values()[column].name());
	}

	public Class<?> getColumnClass(int column) {
		return Columns.values()[column].columnClass;
	}

	public Object getValueAt(Object object, int column) {
		final SearchDataNode node = (SearchDataNode) object;
		switch (Columns.values()[column]) {
		case NAME:
			return node.getName();
		case TYPE:
			return node.getType();
		case TYPEVALUE:
			return node.getTypeValue();
		case VALUE:
			return node.getConstaintValue();
		default:
			break;
		}
		return null;
	}

	public Iterable<VertexConstraint> getConstraints() {
		return new SearchDataNodeTransformer().transform(root);
	}

	@Override
	public Object getRoot() {
		return root;
	}

	@Override
	public boolean isCellEditable(Object object, int column) {
		return true;
	}

	@Override
	public void setValueAt(Object value, Object objectNode, int column) {
		final SearchDataNode node = (SearchDataNode) objectNode;
		switch (Columns.values()[column]) {
		case NAME:
			node.setName((String) value);
			break;
		case TYPE:
		case TYPEVALUE:
		case VALUE:
			switch (node.getType()) {
			case Number:
				node.setNumber((BigDecimal) value);
				break;
			case Boolean:
				node.setBool((Boolean) value);
				break;
			default:
				LOGGER.warn("unknown type " + node.getType());
			}
		default:
			
		}
		super.setValueAt(value, node, column);
	}
	/*
	 * public boolean isCellEditable(Object node, int column) { return true; //
	 * Important to activate TreeExpandListener }
	 * 
	 * public void setValueAt(Object aValue, Object node, int column) { }
	 */

}
