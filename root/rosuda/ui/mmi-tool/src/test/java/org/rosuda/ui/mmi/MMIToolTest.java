package org.rosuda.ui.mmi;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    public void theModelContainsTheExpectedSelectableNodes() {
	assertThat((TreeNode) model.getUniqueStructure().getRoot(), isA(TreeNode.class));
	assertThat(model.getUniqueStructure().getRoot(), notNullValue());
	//TODO pfad muss extrahiert werden
    }

    @Test
    public void aGivenModelNodeSelectionWillShowTheExpectedResultsOnTheTableWhenSynchronizeIsClicked() {
	assertThat(view.getMMITable().getValue(), sameInstance((TableModel) model.getTableModel()));
	assertThat(view.getMMITable().getValue().getRowCount(), equalTo(8));
	assertThat(view.getMMITable().getValue().getColumnCount(), equalTo(0));

	//TODO reale werte model "name" (=r object name), df, AIC, coefficient/ .. /p-value
	NodeTreeSelection selection = new NodeTreeSelection.Impl(Arrays.asList(NodePath.Impl.parse("/"), NodePath.Impl.parse("call/rsymbol[0]"),
		NodePath.Impl.parse("df/Integer[0]")));
	;
	// select something on the unique tree structure .. [must be contained
	// in the selection]
	view.getUniqueStructureSelection().setValue(selection);
    }

    @Test
    public void additionalCalculatedValuesAreDisplayedInTheTable() {

    }

    // -- helper
    @SuppressWarnings("unchecked")
    private static Node<IREXP> loadResource(final String resourceName) throws IOException, ClassNotFoundException {
	final ObjectInputStream ois = new ObjectInputStream(MMIToolTest.class.getResourceAsStream(resourceName));
	final Object rootNode = ois.readObject();
	ois.close();
	return (Node<IREXP>) rootNode;
    }

}
