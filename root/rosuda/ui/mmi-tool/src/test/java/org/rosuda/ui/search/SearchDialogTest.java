package org.rosuda.ui.search;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.awt.event.MouseEvent;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.jdesktop.swingx.treetable.TreeTableModel;
import org.junit.Before;
import org.junit.Test;
import org.rosuda.ui.core.mvc.DefaultHasClickable;
import org.rosuda.ui.core.mvc.DefaultHasValue;
import org.rosuda.ui.core.mvc.DefaultTestView;
import org.rosuda.ui.core.mvc.HasClickable;
import org.rosuda.ui.core.mvc.HasClickable.ClickEvent;
import org.rosuda.ui.core.mvc.HasValue;
import org.rosuda.ui.core.mvc.MessageBus;
import org.rosuda.ui.search.SearchDataNode.ConstraintType;

public class SearchDialogTest {

    private SearchDialogModel model;
    private SearchDialogView<Object> view;
    private SearchDialogPresenter<Object> presenter;
    private MessageBus mb;

    private static class SearchDialogObjectView extends DefaultTestView implements SearchDialogView<Object> {

	private final HasClickable closeButton = new DefaultHasClickable();
	private final HasClickable searchButton = new DefaultHasClickable();
	private final HasClickable add = new DefaultHasClickable();
	private final HasClickable remove = new DefaultHasClickable();
	private final HasValue<String> name = new DefaultHasValue<String>();
	private final HasValue<TreeTableModel> treeTableModel = new DefaultHasValue<TreeTableModel>();
	private final HasValue<TreeSelectionModel> treeSelectionModel = new HasValue<TreeSelectionModel>() {

	    final TreeSelectionModel model = new DefaultTreeSelectionModel();
	    @Override
	    public TreeSelectionModel getValue() {
		return model;
	    }

	    @Override
	    public void setValue(TreeSelectionModel value) {
		throw new UnsupportedOperationException();
	    }

	    @Override
	    public void addChangeListener(org.rosuda.ui.core.mvc.HasValue.ValueChangeListener<TreeSelectionModel> listener) {
	    }

	    @Override
	    public void removeChangeListener(org.rosuda.ui.core.mvc.HasValue.ValueChangeListener<TreeSelectionModel> listener) {
	    }
	    
	};
	private final HasValue<ConstraintType> constraint = new DefaultHasValue<ConstraintType>();
	
	@Override
	public HasClickable getCloseButton() {
	    return closeButton;
	}

	@Override
	public HasClickable getSearchButton() {
	    return searchButton;
	}

	@Override
	public HasClickable getAddToTree() {
	    return add;
	}

	@Override
	public HasClickable getRemoveFromTree() {
	    return remove;
	}

	@Override
	public HasValue<String> getNodeNameInput() {
	    return name;
	}

	@Override
	public HasValue<ConstraintType> getNodeConstraintType() {
	    return constraint;
	}

	@Override
	public HasValue<TreeTableModel> getTreeTableModel() {
	   return treeTableModel;
	}

	@Override
	public HasValue<TreeSelectionModel> getTreeSelectionModel() {
	    return treeSelectionModel;
	}

    }

    @Before
    public void setUp() throws IOException, ClassNotFoundException {
	mb = new MessageBus.Impl();
	presenter = new SearchDialogPresenter<Object>();
	model = new SearchDialogModel();

	final SearchTreeModel searchTreeModel = new SearchTreeModel();
	model.setSearchTreeModel(searchTreeModel);
	view = new SearchDialogObjectView();
	presenter.bind(model, view, mb);
    }

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
	view.getTreeSelectionModel().getValue().setSelectionPath(null);
	
	view.getNodeNameInput().setValue("nodeName");
	view.getNodeConstraintType().setValue(ConstraintType.Name);
	clickAddToTreeButton();

	assertThat(model.getSearchTreeModel().getRoot(), notNullValue());
	assertThat(((SearchDataNode)model.getSearchTreeModel().getRoot()).getName(), equalTo("nodeName"));

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

    // -- helper

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
	((DefaultHasClickable) view.getAddToTree()).click(new ClickEvent() {

	    @Override
	    public Set<Modifier> getModifiers() {
		return Collections.emptySet();
	    }

	    @Override
	    public int getClickCount() {
		return 1;
	    }

	    @Override
	    public int getButton() {
		return MouseEvent.BUTTON1;
	    }
	});
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
