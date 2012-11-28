package org.rosuda.ui.mmi;

import java.util.Collection;
import java.util.ResourceBundle;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.event.ListDataListener;

import org.rosuda.mvc.swing.TypedDynamicListModel;
import org.rosuda.mvc.swing.model.SelectionListModelAdapter;
import org.rosuda.type.Node;
import org.rosuda.ui.context.UIContext;
import org.rosuda.ui.core.mvc.MessageBus;
import org.rosuda.ui.dialog.RootNodeWrapper;
import org.rosuda.visualizer.Localized;
import org.rosuda.visualizer.NodeTreeModel;

public class IRMMISpreadSheet<T> extends JDialog {

    private static final long serialVersionUID = -7528925723342644500L;
    
    public class JDialogImpl {

	//TODO remove dependency from presenter *JDialog* to View
	private final MMIToolPresenter<T, JDialog> presenter;
	private final MMIToolModel<T> model;
	private final MMIToolViewDialogImpl<T> view;

	public JDialogImpl(UIContext context, final Collection<Node<T>> data) throws Exception {
	    this.presenter = new MMIToolPresenter<T, JDialog>();
	    this.model = new MMIToolModel<T>();
	    this.model.setUniqueStructure(new NodeTreeModel<T>(new RootNodeWrapper<T>(null, data)));
	    this.model.setTableModel(new MMIDynamicTableModel<T>(data));
	    this.model.setExpressionListSelectionModel(new SelectionListModelAdapter<String>());
	    this.model.setExpressionListModel(new TypedDynamicListModel.Impl<String>());
	    this.view = new MMIToolViewDialogImpl<T>(context);
	    presenter.bind(model, view, context.getAppContext().getBean(MessageBus.class));
	}

    }

    public IRMMISpreadSheet(final UIContext context, final Collection<Node<T>> data) throws Exception {
	super(context.getUIFrame(), ModalityType.MODELESS);
	final ResourceBundle localization = ResourceBundle.getBundle(IRMMISpreadSheet.class.getName());
	final Localized localized = new Localized.ResourceBundleImpl(localization);
	if (data.isEmpty()) {
	    JOptionPane.showConfirmDialog(context.getUIFrame(), localized.get("NO_RESULT"), localized.get("ALERT"), JOptionPane.OK_OPTION);
	} else {
	    new JDialogImpl(context, data);
	}
    }
}
