package org.rosuda.ui.mmi;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.rosuda.type.NodePath;
import org.rosuda.type.NodePath.Identifier;
import org.rosuda.ui.core.mvc.HasClickable.ClickEvent;
import org.rosuda.ui.core.mvc.HasClickable.ClickListener;
import org.rosuda.ui.core.mvc.HasValue.ValueChangeListener;
import org.rosuda.ui.core.mvc.MVP;
import org.rosuda.ui.core.mvc.MessageBus;
import org.rosuda.visualizer.NodeTreeSelection;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class MMIToolPresenter<T, C> implements MVP.Presenter<MMIToolModel<T>, MMIToolView<T, C>> {

    private ValueChangeListener<NodeTreeSelection> uniqueStructureListener;
    private ClickListener createExpressionListener;
    private ListSelectionListener expressionListSelectionListener;

    @Override
    public void bind(final MMIToolModel<T> model, final MMIToolView<T, C> view, final MessageBus mb) {
	initListeners(model, view);
	view.getUniqueStructureTree().setValue(model.getUniqueStructure());
	view.getMMITable().setValue(model.getTableModel());
	view.getExpressionListSelection().setValue(model.getExpressionListSelectionModel());
	view.getExpressionListModel().setValue(model.getExpressionListModel());
	view.getUniqueStructureSelection().addChangeListener(uniqueStructureListener);
	view.getCreateExpressionButton().addClickListener(createExpressionListener);
	view.getExpressionListSelection().getValue().addListSelectionListener(expressionListSelectionListener);
	view.show();

    }

    private NodeTreeSelection mergeSelection(NodeTreeSelection nodeTreeSelection, List<String> expressions) {
	final List<NodePath> paths = new ArrayList<NodePath>();
	if (nodeTreeSelection != null) {
	    paths.addAll(nodeTreeSelection.getSelectedPaths());
	}
	if (expressions != null) {
	    paths.addAll(Lists.transform(expressions, new Function<String, NodePath>() {
		@Override
		@Nullable
		public NodePath apply(@Nullable String input) {
		    if (input == null) {
			return null;
		    }
		    final Identifier id = new Identifier.Impl(input);
		    return new NodePath.Impl(id);
		}
	    }));
	}
	return new NodeTreeSelection.Impl(paths);
    }
    
    private List<String> getSelectedExpressionsFromList(final MMIToolModel<T> model) {
	final List<String> expressions = new ArrayList<String>();
	final ListSelectionModel selectionModel = model.getExpressionListSelectionModel();
	for (int i=selectionModel.getMinSelectionIndex();i<=selectionModel.getMaxSelectionIndex();i++) {
	    if (selectionModel.isSelectedIndex(i)) {
		expressions.add(model.getExpressionListModel().at(i));
	    }
	}
	return expressions;
    }

    @Override
    public void unbind(MMIToolModel<T> model, MMIToolView<T, C> view, MessageBus mb) {
	view.getUniqueStructureSelection().removeChangeListener(uniqueStructureListener);
	view.getCreateExpressionButton().removeClickListener(createExpressionListener);
	view.getExpressionListSelection().getValue().removeListSelectionListener(expressionListSelectionListener);
    }

    // -- helper
    private void initListeners(final MMIToolModel<T> model, final MMIToolView<T, C> view) {
	uniqueStructureListener = new ValueChangeListener<NodeTreeSelection>() {
	    @Override
	    public void onValueChange(NodeTreeSelection newValue) {
		final List<String> expressions = MMIToolPresenter.this.getSelectedExpressionsFromList(model);
		model.getTableModel().updateSelection(MMIToolPresenter.this.mergeSelection(newValue, expressions));
	    }

	   
	};
	createExpressionListener = new ClickListener() {
	    @Override
	    public void onClick(ClickEvent event) {
		String expression = view.getExpressionField().getValue();
		if (expression == null || expression.trim().length() == 0) {
		    return;
		}
		model.getExpressionListModel().add(expression);
	    }
	};
	expressionListSelectionListener = new ListSelectionListener() {

	    @Override
	    public void valueChanged(ListSelectionEvent e) {
		final List<String> expressions = MMIToolPresenter.this.getSelectedExpressionsFromList(model);
		final NodeTreeSelection nodeTreeSelection = view.getUniqueStructureSelection().getValue();
		model.getTableModel().updateSelection(MMIToolPresenter.this.mergeSelection(nodeTreeSelection, expressions));
	    }

	};
    }

}
