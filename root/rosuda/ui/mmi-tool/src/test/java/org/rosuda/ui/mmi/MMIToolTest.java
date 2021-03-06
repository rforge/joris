package org.rosuda.ui.mmi;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.io.IOException;
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
import org.rosuda.type.NodeFinder;
import org.rosuda.type.NodeFinderImpl;
import org.rosuda.type.NodePath;
import org.rosuda.ui.core.mvc.DefaultHasClickable;
import org.rosuda.ui.core.mvc.TestUtil;
import org.rosuda.ui.test.MVPTest;
import org.rosuda.visualizer.NodeTreeModel;
import org.rosuda.visualizer.NodeTreeSelection;

public class MMIToolTest extends MVPTest<MMIToolModel<IREXP>, MMIToolView<IREXP, Void>, MMIToolPresenter<IREXP, Void>, MMIToolTestModelData> {
 
    private NodeFinder<IREXP> nodeFinder;
    
    @Before
    public void setUp() throws IOException, ClassNotFoundException {
	nodeFinder = new NodeFinderImpl<IREXP>();
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

	assertThat(view.getMMITable().getValue().getRowCount(), equalTo(8));
	assertThat(view.getMMITable().getValue().getColumnCount(), equalTo(3));
    }

    @Test
    public void uniqueTreeNodesAreEnsured() {
	final TreeNode root = getAssertedTreeNodeRoot();
	assertThatChildNamesAtLevelAreUnique(root, 1);
	// matrix nur ein mal .. teste struktur
    }

    @Test
    public void expectedNodeValuesAreSelectableAndNotNull() {
	final TreeNode root = getAssertedTreeNodeRoot();
	assertThat(findChildByPath(root, Arrays.asList("coefficients", "matrix", "(Intercept)", "Estimate")), notNullValue());

    }

    @Test
    public void expectedLeafValuesAreSelectableAndNotNull() {
	final TreeNode root = getAssertedTreeNodeRoot();
	assertThat(findChildByPath(root, Arrays.asList("coefficients", "matrix", "(Intercept)", "Estimate", "Double")), nullValue());
	assertThat(findChildByPath(root, Arrays.asList("coefficients", "matrix", "(Intercept)", "Estimate")), instanceOf(TreeNode.class));
	final TreeNode leaf = (TreeNode) findChildByPath(root, Arrays.asList("coefficients", "matrix", "(Intercept)", "Estimate"));
	assertThat(leaf.getChildCount(), equalTo(0));
    }

    @Test
    public void iCanEnterACalculatedValue() {
	view.getExpressionField().setValue("2*${AIC})");
	TestUtil.simulateLeftClick((DefaultHasClickable) view.getCreateExpressionButton());
	assertThat(model.getExpressionListModel().getSize(), equalTo(1));
    }

    @Test
    public void selectedAdditionalCalculatedValuesAreDisplayedInTheTable() {
	addAdditionalExpressionValue();
	assertThat(view.getMMITable().getValue().getRowCount(), equalTo(8));
	assertThat(view.getMMITable().getValue().getColumnCount(), equalTo(1));
    }

    @Test
    public void additionalValuesAreCalculatedCorrectly() {
	addAdditionalExpressionValue();
	// TODO get value from $AIC from the model
	for (int row = 0; row < view.getMMITable().getValue().getRowCount(); row++) {
	    final double AIC = getDataValue(modelInitializer.dataAt(row), "AIC");
	    assertThat((Double) view.getMMITable().getValue().getValueAt(row, 0), equalTo(2.0 * AIC));
	}
    }

    @Test
    public void weightedValuesAreCalculatedAsExpected() {
	addAdditionalExpressionValue("${AIC}");
	// TODO somethink like that, named field ?!
	addAdditionalExpressionValue("AICdiff := ${AIC}-cmin(${AIC})");
	addAdditionalExpressionValue("AIClikelihood := exp(-0.5*${@AICdiff})");
	addAdditionalExpressionValue("AICratio := ${@AIClikelihood}/csum(${@AIClikelihood})");
	// addAdditionalExpressionValue("AICratio2 := exp(-0.5*${@AICdiff})/csum(exp(-0.5*${@AICdiff}))");
	view.getExpressionListSelection().getValue().setSelectionInterval(0, 3);
	double aicMin = Double.MAX_VALUE;
	for (int row = 0; row < view.getMMITable().getValue().getRowCount(); row++) {
	    final double AIC = getDataValue(modelInitializer.dataAt(row), "AIC");
	    aicMin = Math.min(aicMin, AIC);
	    assertThat((Double) view.getMMITable().getValue().getValueAt(row, 0), equalTo(AIC));
	}
	final List<Double> aicLikelihoods = new ArrayList<Double>();
	for (int row = 0; row < view.getMMITable().getValue().getRowCount(); row++) {
	    final double AICdiff = getDataValue(modelInitializer.dataAt(row), "AIC") - aicMin;
	    aicLikelihoods.add(Math.exp(-0.5 * AICdiff));
	    assertThat("likelihood failed at index " + row, (Double) view.getMMITable().getValue().getValueAt(row, 1), equalTo(AICdiff));
	}
	double aicLikelihoodSum = 0.0;
	for (final double aicLikelihood : aicLikelihoods) {
	    aicLikelihoodSum += aicLikelihood;
	}
	// TODO: does not work current value: 1.045e-15, expected 9.4e14
	for (int row = 0; row < view.getMMITable().getValue().getRowCount(); row++) {
	    assertThat("weight failed at index " + row, (Double) view.getMMITable().getValue().getValueAt(row, 2), equalTo(aicLikelihoods.get(row)));
	}
	for (int row = 0; row < view.getMMITable().getValue().getRowCount(); row++) {
	    assertThat("ratio failed at index " + row, (Double) view.getMMITable().getValue().getValueAt(row, 3), equalTo(aicLikelihoods.get(row)
		    / aicLikelihoodSum));
	}
    }

    @Test
    public void listAndTreeSelectionMixIntoTheTable() {
	aGivenModelNodeSelectionWillShowTheExpectedResultsOnTheTableWhenSynchronizeIsClicked();
	addAdditionalExpressionValue();

	assertThat(view.getMMITable().getValue().getRowCount(), equalTo(8));
	assertThat(view.getMMITable().getValue().getColumnCount(), equalTo(4));
    }

    // -- helper
   
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

    private void addAdditionalExpressionValue() {
	addAdditionalExpressionValue("2*${AIC}");
	final int rootChildrenBefore = view.getMMITable().getValue().getColumnCount();
	view.getExpressionListSelection().getValue().setSelectionInterval(0, 0);
	assertThat(view.getMMITable().getValue().getColumnCount(), equalTo(rootChildrenBefore + 1));
    }

    private void addAdditionalExpressionValue(String valueExpr) {
	view.getExpressionField().setValue(valueExpr);
	TestUtil.simulateLeftClick((DefaultHasClickable) view.getCreateExpressionButton());
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

    private double getDataValue(Node<IREXP> node, String string) {
	final NodePath path = NodePath.Impl.parse(string);
	return nodeFinder.findNode(node, path).getValue().getNumber().doubleValue();
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
