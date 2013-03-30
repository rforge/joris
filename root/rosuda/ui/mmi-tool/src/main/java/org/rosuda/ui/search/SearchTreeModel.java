package org.rosuda.ui.search;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.treetable.AbstractTreeTableModel;
import org.rosuda.graph.service.search.Relation;
import org.rosuda.graph.service.search.VertexConstraint;
import org.rosuda.ui.search.SearchDataNode.ConstraintType;
import org.rosuda.visualizer.Localized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchTreeModel extends AbstractTreeTableModel implements Serializable, TableModel {

    private static final long serialVersionUID = 6527804524956955313L;
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchTreeModel.class);

    enum Columns {

        NAME(String.class) ,
        TYPE(ConstraintType.class) ,
        TYPEVALUE(Enum.class) ,
        VALUE(Object.class);

        private Class<?> columnClass;

        Columns(final Class<?> columnClass) {

        }
    }

    private SearchDataNode root;
    private final Localized localized;

    public SearchTreeModel() {
        final ResourceBundle localization = ResourceBundle.getBundle(SearchTreeModel.class.getName());
        this.localized = new Localized.ResourceBundleImpl(localization);
        final SearchDataNode rootNode = new SearchDataNode(localization.getString("root"), ConstraintType.Name);
        this.setRoot(rootNode);
        final SearchDataNode coefficients = new SearchDataNode("coefficients", ConstraintType.Name);
        root.addChild(coefficients);
        final SearchDataNode matrix = new SearchDataNode("matrix", ConstraintType.Name);
        coefficients.addChild(matrix);
        final SearchDataNode distNode = new SearchDataNode("dist", ConstraintType.Name);
        matrix.addChild(distNode);

        final MathContext precion2 = new MathContext(2, RoundingMode.HALF_UP);
        distNode.addChild(new SearchDataNode("Estimate", ConstraintType.Name).addChild(new SearchDataNode(null, ConstraintType.Number)
                .setTypeValue(Relation.GT).setNumber(BigDecimal.ZERO)));
        distNode.addChild(new SearchDataNode("Estimate", ConstraintType.Name).addChild(new SearchDataNode(null, ConstraintType.Number)
                .setTypeValue(Relation.LT).setNumber(new BigDecimal(10, precion2))));

        distNode.addChild(new SearchDataNode("Pr(>|t|)", ConstraintType.Name).addChild(new SearchDataNode(null, ConstraintType.Number)
                .setTypeValue(Relation.LT).setNumber(new BigDecimal(0.15, precion2))));
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
        final List<VertexConstraint> constaints = new ArrayList<VertexConstraint>();
        for (final SearchDataNode child : root.getChildren()) {
            constaints.addAll(new SearchDataNodeTransformer().transform(child));
        }
        return constaints;
    }

    @Override
    public Object getRoot() {
        return root;
    }

    @Override
    public boolean isCellEditable(Object object, int column) {
        return column > 0;
    }

    @Override
    public void setValueAt(Object value, Object objectNode, int column) {
        final SearchDataNode node = (SearchDataNode) objectNode;
        switch (Columns.values()[column]) {
        case NAME:
            node.setName((String) value);
            break;
        case TYPE:
            node.setType((ConstraintType) value);
            break;
        case TYPEVALUE:
            node.setTypeValue((Enum<?>) value);
            break;
        case VALUE:
            switch (node.getType()) {
            case Number:
                node.setNumber((BigDecimal) value);
                break;
            case Boolean:
                node.setBool((Boolean) value);
                break;
            case String:
                node.setString((String) value);
                break;
            case Name:
                node.setName((String) value);
                break;
            default:
                LOGGER.warn("unknown type " + node.getType());
            }
        default:

        }
        super.setValueAt(value, node, column);
    }

    public void setRoot(SearchDataNode node) {
        this.root = node;
        super.root = node;
    }

    public void removedChild(final TreePath path, final int index, final Object child) {
        super.modelSupport.fireChildRemoved(path, index, child);
    }

    public void addedChild(final TreePath path, final int index, final Object child) {
        if (path == null) {
            super.modelSupport.fireNewRoot();
        } else {
            super.modelSupport.fireChildAdded(path, index, child);
        }
    }

    @Override
    public int getRowCount() {
        // TODO set
        // have flat SeachtDataList
        return 5;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex > 1;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        // TODO Auto-generated method stub

    }

    @Override
    public void addTableModelListener(TableModelListener l) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
        // TODO Auto-generated method stub

    }
}
