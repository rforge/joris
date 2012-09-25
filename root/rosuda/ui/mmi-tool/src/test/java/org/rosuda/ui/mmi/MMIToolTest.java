package org.rosuda.ui.mmi;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.table.TableModel;
import javax.swing.tree.TreeNode;

import org.junit.Before;
import org.junit.Test;
import org.rosuda.irconnect.IREXP;
import org.rosuda.type.Node;
import org.rosuda.type.NodePath;
import org.rosuda.ui.core.mvc.DefaultHasClickable;
import org.rosuda.ui.core.mvc.DefaultHasValue;
import org.rosuda.ui.core.mvc.DefaultTestView;
import org.rosuda.ui.core.mvc.HasClickable;
import org.rosuda.ui.core.mvc.HasValue;
import org.rosuda.ui.core.mvc.MessageBus;
import org.rosuda.ui.dialog.RootNodeWrapper;
import org.rosuda.visualizer.NodeTreeModel;
import org.rosuda.visualizer.NodeTreeSelection;

public class MMIToolTest {

    private MMIToolModel<IREXP> model;
    private MMIToolView<IREXP, Object> view;
    private MMIToolPresenter<IREXP, Object> presenter;
    private MessageBus mb;
    private List<Node<IREXP>> data;

    private static class MMIToolTestObjectView extends DefaultTestView implements MMIToolView<IREXP, Object> {

	private HasValue<NodeTreeModel<IREXP>> uniqueStructureTree = new DefaultHasValue<NodeTreeModel<IREXP>>();
	private HasValue<NodeTreeSelection> uniqueStructureSelection = new DefaultHasValue<NodeTreeSelection>();
	private HasValue<TableModel> mmiTable = new DefaultHasValue<TableModel>();
	private final HasClickable synchronizeTreeToTable = new DefaultHasClickable();

	@Override
	public HasValue<NodeTreeModel<IREXP>> getUniqueStructureTree() {
	    return uniqueStructureTree;
	}

	@Override
	public HasValue<NodeTreeSelection> getUniqueStructureSelection() {
	    return uniqueStructureSelection;
	}

	@Override
	public HasValue<TableModel> getMMITable() {
	    return mmiTable;
	}

	@Override
	public HasClickable getSynchronizeTreeToTable() {
	    return synchronizeTreeToTable;
	}

    }

    @Before
    public void setUp() throws IOException, ClassNotFoundException {
	data = new ArrayList<Node<IREXP>>();
	for (int i = 1; i <= 8; i++) {
	    final String rscName = "/models/airquality-" + i + ".rObj";
	    data.add(loadResource(rscName));
	}

	mb = new MessageBus.Impl();
	presenter = new MMIToolPresenter<IREXP, Object>();
	model = new MMIToolModel<IREXP>();

	final MMIDynamicTableModel<IREXP> tableModel = new MMIDynamicTableModel<IREXP>(data);
	model.setTableModel(tableModel);
	model.setUniqueStructure(new NodeTreeModel<IREXP>(new RootNodeWrapper<IREXP>(null, data)));

	view = new MMIToolTestObjectView();
	presenter.bind(model, view, mb);
    }

    @Test
    public void aGivenModelNodeSelectionWillShowTheExpectedResultsOnTheTableWhenSynchronizeIsClicked() {
	assertThat(view.getMMITable().getValue(), sameInstance((TableModel) model.getTableModel()));
	assertThat(view.getMMITable().getValue().getRowCount(), equalTo(8));
	assertThat(view.getMMITable().getValue().getColumnCount(), equalTo(0));

	// TODO reale werte model "name" (=r object name), df, AIC, coefficient/
	// .. /p-value
	NodeTreeSelection selection = new NodeTreeSelection.Impl(Arrays.asList(NodePath.Impl.parse("/"), NodePath.Impl.parse("call/rsymbol[0]"),
		NodePath.Impl.parse("df/Integer[0]")));
	;
	// select something on the unique tree structure .. [must be contained
	// in the selection]
	view.getUniqueStructureSelection().setValue(selection);
    }

    @Test
    public void uniqueTreeNodesAreEnsured() {
	final TreeNode root = getAssertedTreeNodeRoot();
	assertThatChildNamesAtLevelAreUnique(root, 1);
	// matrix nur ein mal .. teste struktur
    }

    @Test
    public void expectedLeafValuesAreSelectableAndNotNull() {
	final TreeNode root = getAssertedTreeNodeRoot();
	assertThat(findChildByPath(root, Arrays.asList("coefficients", "matrix", "(Intercept)", "Estimate")), notNullValue());

    }

    @Test
    public void additionalCalculatedValuesAreDisplayedInTheTable() {
	// rechne AIC ... aus
    }

    // -- helper
    @SuppressWarnings("unchecked")
    private static Node<IREXP> loadResource(final String resourceName) throws IOException, ClassNotFoundException {
	final ObjectInputStream ois = new ObjectInputStream(MMIToolTest.class.getResourceAsStream(resourceName));
	final Object rootNode = ois.readObject();
	ois.close();
	return (Node<IREXP>) rootNode;
    }

    private TreeNode getAssertedTreeNodeRoot() {
	NodeTreeModel<IREXP> uniqueStructureTree = view.getUniqueStructureTree().getValue();
	assertThat(uniqueStructureTree, notNullValue());
	final Object root = uniqueStructureTree.getRoot();
	assertThat(root, instanceOf(TreeNode.class));
	return (TreeNode) root;
    }

    private void assertThatChildNamesAtLevelAreUnique(final TreeNode parent, final int level) {
	final Set<String> namesAtLevel = new TreeSet<String>();
	for (int i = 0; i < parent.getChildCount(); i++) {
	    final TreeNode child = parent.getChildAt(i);
	    String childName = getChildNameFromTreeNodeWrapper(child);
	    if (childName == null)
		continue;
	    assertThat("not unique node at level " + level + ", path: " + createPath(child), namesAtLevel, not(contains(childName)));
	    namesAtLevel.add(childName);
	    assertThatChildNamesAtLevelAreUnique(child, level);
	}
    }

    private String getChildNameFromTreeNodeWrapper(final TreeNode child) {
	if (child == null)
	    return null;
	return child.toString();
    }

    private List<String> createPath(TreeNode child) {
	final List<String> path = new ArrayList<String>();
	buildPath(child, path);
	Collections.reverse(path);
	return Collections.unmodifiableList(path);
    }

    private void buildPath(TreeNode child, final List<String> path) {
	if (child == null)
	    return;
	else {
	    path.add(getChildNameFromTreeNodeWrapper(child));
	    buildPath(child.getParent(), path);
	}
    }

    private Object findChildByPath(final TreeNode parent, List<String> asList) {
	if (parent == null)
	    return null;
	if (asList.isEmpty())
	    return parent;
	final String nodeName = asList.get(0);
	for (int i = 0; i < parent.getChildCount(); i++) {
	    final TreeNode child = parent.getChildAt(i);
	    final String childName = getChildNameFromTreeNodeWrapper(child);
	    if (nodeName.equals(childName)) {
		return findChildByPath(child, asList.subList(1, asList.size()));
	    }
	}
	return null;
    }

}
