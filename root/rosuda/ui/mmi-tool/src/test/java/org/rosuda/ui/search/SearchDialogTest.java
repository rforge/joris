package org.rosuda.ui.search;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.event.TreeModelEvent;
import javax.swing.tree.TreePath;

import org.apache.log4j.lf5.viewer.categoryexplorer.TreeModelAdapter;
import org.jdesktop.swingx.treetable.TreeTableModel;
import org.junit.Test;
import org.rosuda.graph.service.search.Relation;
import org.rosuda.type.NodePath;
import org.rosuda.ui.core.mvc.DefaultHasClickable;
import org.rosuda.ui.core.mvc.TestUtil;
import org.rosuda.ui.search.SearchDataNode.ConstraintType;
import org.rosuda.ui.test.MVPTest;

public class SearchDialogTest extends MVPTest<SearchDialogModel, SearchDialogView<Void>, SearchDialogPresenter<Void>, SearchDialogTestModelData>{
   
    @Test
    public void bindingCheck() {
	assertThat(view.getTreeTableModel(), notNullValue());
	assertThat(view.getTreeSelectionModel(), notNullValue());
    }

    @Test
    public void iCanRenameANodeFromTheSearchQueryTree() {
	final SearchDataNode node = prepareChildNode();
	final String oldName = node.getName();
	final String name = "other" + oldName;
	node.setName(name);

	assertThat(node.getName(), equalTo(name));
	assertThat(node.getName(), not(equalTo(oldName)));
    }

    @Test
    public void iCanChangeANumberValueIntheQueryParameterTable() {
	final SearchDataNode node = prepareChildNode();
	final BigDecimal oldValue = node.getNumber();
	final BigDecimal newValue = BigDecimal.ONE;
	node.setNumber(newValue);

	assertThat(node.getNumber(), equalTo(newValue));
	assertThat(node.getNumber(), not(equalTo(oldValue)));
    }

    @Test
    public void iCanChangeAStringValueIntheQueryParameterTable() {
	final SearchDataNode node = prepareChildNode();
	final String oldValue = node.getString();
	final String newValue = "another" + oldValue;
	node.setString(newValue);

	assertThat(node.getString(), equalTo(newValue));
	assertThat(node.getString(), not(equalTo(oldValue)));
    }

    @Test
    public void iCanChangeABooleanValueIntheQueryParameterTable() {
	final SearchDataNode node = prepareChildNode();
	final Boolean oldValue = node.isBool();
	final Boolean newValue = true;
	node.setBool(newValue);

	assertThat(node.isBool(), equalTo(newValue));
	assertThat(node.isBool(), not(equalTo(oldValue)));
    }

    @Test
    public void iCanRemoveANodeFromTheSearchQueryTree() {
	final SearchDataNode node = prepareChildNode();
	final int currentChildCount = node.getChildren().size();
	assertThat(currentChildCount, greaterThanOrEqualTo(1));

	node.removeChild(node.getChildren().get(0));

	assertThat(node.getChildren().size() - currentChildCount, equalTo(-1));
    }

    @Test
    public void iCanAddANodeToTheSearchQueryTree() {
	final SearchDataNode node = prepareChildNode();
	final int currentChildCount = node.getChildren().size();
	assertThat(currentChildCount, greaterThanOrEqualTo(1));
	node.addChild(new SearchDataNode(null, null));

	assertThat(node.getChildren().size() - currentChildCount, equalTo(1));
    }

    @Test
    public void whenISetANameAndConstraintAndClickInsertIntoAnEmptyTreeIGetANewRoot() {
	model.getSearchTreeModel().setRoot(null);
	
	view.getNodeNameInput().setValue("nodeName");
	view.getNodeConstraintType().setValue(ConstraintType.Name);

	final List<TreeModelEvent> events = new ArrayList<TreeModelEvent>();
	model.getSearchTreeModel().addTreeModelListener(new TreeModelAdapter(){
	    @Override
	    public void treeNodesInserted(TreeModelEvent e) {
	        org.slf4j.LoggerFactory.getLogger(SearchDialogTest.class).warn("*** INSERT EVENT");
	        events.add(e);
	        super.treeNodesInserted(e);
	    }
	    @Override
	    public void treeStructureChanged(TreeModelEvent e) {
		events.add(e);
		org.slf4j.LoggerFactory.getLogger(SearchDialogTest.class).warn("*** STRUCTURE CHANGE");
	        super.treeStructureChanged(e);
	    }
	});
	clickAddToTreeButton();
	
	org.slf4j.LoggerFactory.getLogger(SearchDialogTest.class).warn(">>> recorded Events: "+events);

	assertThat(((SearchDataNode) model.getSearchTreeModel().getRoot()).getName(), equalTo("nodeName"));
    }

    @Test
    public void whenISetANameAndConstraintAndClickInsertThereIsANewNodeAsLastChild() {
	final SearchDataNode node = prepareChildNode();
	view.getTreeSelectionModel().getValue().setSelectionPath(toTreePath(node));

	final int currentChildCount = node.getChildren().size();
	view.getNodeNameInput().setValue("nodeName");
	view.getNodeConstraintType().setValue(ConstraintType.Name);
	clickAddToTreeButton();

	assertThat(node.getChildren().size() - currentChildCount, equalTo(1));

    }

    @Test
    public void whenISelectASingleNodeInATreePathICanRemoveTheSelectedChildFromTheModel() {
	final SearchDataNode node = prepareChildNode();
	final SearchDataNode parentNode = node.getParent();
	view.getTreeSelectionModel().getValue().setSelectionPath(toTreePath(node));

	final int currentChildCount = parentNode.getChildren().size();
	clickRemoveFromTreeButton();

	assertThat(parentNode.getChildren().size() - currentChildCount, equalTo(-1));
    }

    @Test
    public void whenIUseTheTableViewColumn3ToUpdateANodeInTheModelTheModelIsChanged() {
	final SearchDataNode affectedNode = prepareAffectedNode();
	assertThat(affectedNode.getNumber().intValue(), equalTo(0));
	view.getTreeTableModel().getValue().setValueAt(new BigDecimal(13), affectedNode, 3);
	assertThat(affectedNode.getNumber().intValue(), equalTo(13));
    }

    @Test
    public void whenIUseTheTableViewColumn2ToUpdateANodeInTheModelTheModelIsChanged() {
	final SearchDataNode affectedNode = prepareAffectedNode();
	assertThat((Relation)affectedNode.getTypeValue(), equalTo(Relation.GT));
	view.getTreeTableModel().getValue().setValueAt(Relation.EQ, affectedNode, 2);
	assertThat((Relation)affectedNode.getTypeValue(), equalTo(Relation.EQ));
    }
    
    @Test
    public void whenIUseTheTableViewColumn1ToUpdateANodeInTheModelTheModelIsChanged() {
	final SearchDataNode affectedNode = prepareAffectedNode();
	assertThat(affectedNode.getType(), equalTo(ConstraintType.Number));
	view.getTreeTableModel().getValue().setValueAt(ConstraintType.String, affectedNode, 1);
	assertThat(affectedNode.getType(), equalTo(ConstraintType.String));
    }
    
    //TODO later implement drop and drag
    
    // -- helper

    private SearchDataNode prepareAffectedNode() {
   	SearchDataNode affectedParentNode = getNode(NodePath.Impl.parse("coefficients/matrix/dist/Estimate"));
   	final SearchDataNode affectedNode = affectedParentNode.getChildren().get(0);
   	return affectedNode;
       }
    
    private SearchDataNode getNode(final NodePath path) {
	return getNode((SearchDataNode) model.getSearchTreeModel().getRoot(), path);
    }
    
    private SearchDataNode getNode(final SearchDataNode parent, final NodePath path) {
   	if (path == null) {
   	    return parent;
   	}
   	final String requiredNodeName = path.getId().getName();
   	for (final SearchDataNode child: parent.getChildren()) {
   	    if (requiredNodeName.equals(child.getName())) {
   		if (path.hasNext()) {
   		    return getNode(child, path.next());
   		} else {
   		    return child;
   		}
   	    }
   	}
   	return null;
      }
    
    private TreePath toTreePath(SearchDataNode node) {
	final List<SearchDataNode> elements = new ArrayList<SearchDataNode>();
	elements.add(node);
	SearchDataNode parent = node.getParent();
	while (parent != null) {
	    elements.add(parent);
	    parent = parent.getParent();
	}
	Collections.reverse(elements);
	return new TreePath(elements.toArray());
    }

    private void clickAddToTreeButton() {
	DefaultHasClickable clickable = (DefaultHasClickable) view.getAddToTree();
	TestUtil.simulateLeftClick(clickable);
    }

    private void clickRemoveFromTreeButton() {
	DefaultHasClickable clickable = (DefaultHasClickable) view.getRemoveFromTree();
	TestUtil.simulateLeftClick(clickable);
    }

    private String getChildNameFromTreeNodeWrapper(final SearchDataNode child) {
	if (child == null)
	    return null;
	return child.toString();
    }

    private SearchDataNode findChildByPath(final SearchDataNode parent, List<String> asList) {
	if (parent == null)
	    return null;
	if (asList.isEmpty())
	    return parent;
	final String nodeName = asList.get(0);
	for (final SearchDataNode child : parent.getChildren()) {
	    final String childName = getChildNameFromTreeNodeWrapper(child);
	    if (nodeName.equals(childName)) {
		return findChildByPath(child, asList.subList(1, asList.size()));
	    }
	}
	return null;
    }

    private SearchDataNode prepareChildNode() {
	TreeTableModel treeModel = view.getTreeTableModel().getValue();

	final SearchDataNode node = findChildByPath((SearchDataNode) treeModel.getRoot(), Arrays.asList("coefficients", "matrix", "dist", "Estimate"));
	assertThat(node, notNullValue());
	return node;
    }
}
