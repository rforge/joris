package org.rosuda.ui.search;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JTextField;
import javax.swing.table.TableColumn;
import javax.swing.text.Document;
import javax.swing.tree.TreeSelectionModel;

import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.TreeTableModel;
import org.rosuda.mvc.swing.DocumentHasValue;
import org.rosuda.mvc.swing.DocumentValueAdapter;
import org.rosuda.mvc.swing.JButtonHasClickable;
import org.rosuda.ui.SwingLayoutProcessor;
import org.rosuda.ui.context.UIContext;
import org.rosuda.ui.core.mvc.HasClickable;
import org.rosuda.ui.core.mvc.HasValue;
import org.rosuda.ui.core.mvc.Screen;
import org.rosuda.ui.search.SearchDataNode.ConstraintType;

public class SearchDialogViewJDialogImpl<C extends JDialog> extends JDialog implements SearchDialogView<C> {

    /**
     * 
     */
    private static final long serialVersionUID = 247567699446989556L;

    public JXTreeTable searchtree = new JXTreeTable();
 
    private JMenuItem close = new JMenuItem();
    private JButton searchbutton = new JButton();
    
    private JMenuItem add = new JMenuItem();
    private JMenuItem remove = new JMenuItem();
    
    private JTextField nodeNameInput = new JTextField();

    private final HasClickable closeButtonInterface;
    private final HasClickable searchButtonInterface;
    private final HasValue<TreeTableModel> searchTreeInterface;
    private final HasValue<TreeSelectionModel> searchTreeSelectionInterface;
    
    HasValue<String> nodeNameInputInterface;
    HasValue<ConstraintType> nodeConstraintInterface;
    HasClickable addToTreeInterface;
    HasClickable removeFromTreeInterface;

    private final UIContext uiContext;

    public SearchDialogViewJDialogImpl(final UIContext context) throws Exception {
	super(context.getUIFrame(), ModalityType.MODELESS);
	this.uiContext = context;
	SwingLayoutProcessor.processLayout(this, "/gui/dialog/ModelSearchDialog.xml");
	
	// TODO searchTree -> selection model
	searchtree.getSelectionModel();
	
	searchButtonInterface = new JButtonHasClickable(searchbutton);
	closeButtonInterface = new JButtonHasClickable(close);
	searchTreeInterface = new HasValue<TreeTableModel>() {

	    private TreeTableModel model;
	    
	    @Override
	    public TreeTableModel getValue() {
		return model;
	    }

	    @Override
	    public void setValue(TreeTableModel value) {
		this.model = value;
		searchtree.setTreeTableModel(value);
	    }

	    @Override
	    public void addChangeListener(org.rosuda.ui.core.mvc.HasValue.ValueChangeListener<TreeTableModel> listener) {
		// TODO Auto-generated method stub
		
	    }

	    @Override
	    public void removeChangeListener(org.rosuda.ui.core.mvc.HasValue.ValueChangeListener<TreeTableModel> listener) {
		// TODO Auto-generated method stub
		
	    }
	};
	
	this.searchTreeSelectionInterface = new HasValue<TreeSelectionModel>() {

	    @Override
	    public TreeSelectionModel getValue() {
		return searchtree.getTreeSelectionModel();
	    }

	    @Override
	    public void setValue(TreeSelectionModel value) {
		System.out.println("unimplemented in jxTree");
		
	    }

	    @Override
	    public void addChangeListener(org.rosuda.ui.core.mvc.HasValue.ValueChangeListener<TreeSelectionModel> listener) {
		// TODO Auto-generated method stub
		
	    }

	    @Override
	    public void removeChangeListener(org.rosuda.ui.core.mvc.HasValue.ValueChangeListener<TreeSelectionModel> listener) {
		// TODO Auto-generated method stub
		
	    }
	    
	};
	
	addToTreeInterface = new JButtonHasClickable(add); 
	removeFromTreeInterface = new JButtonHasClickable(remove);
	final Document document = nodeNameInput.getDocument();
	nodeNameInputInterface = new DocumentHasValue<String>(document, new DocumentValueAdapter.String(document));
    }
    
    @Override
    public HasClickable getSearchButton() {
	return searchButtonInterface;
    }

    @Override
    public HasClickable getCloseButton() {
	return closeButtonInterface;
    }
    
    @Override
    public HasValue<String> getNodeNameInput() {
	return nodeNameInputInterface;
    }

    @Override
    public HasValue<ConstraintType> getNodeConstraintType() {
	return nodeConstraintInterface;
    }

    @Override
    public HasClickable getAddToTree() {
	return addToTreeInterface;
    }

    @Override
    public HasClickable getRemoveFromTree() {
	return removeFromTreeInterface;
    }
    
    @Override
    public HasValue<TreeTableModel> getTreeTableModel() {
	return searchTreeInterface;
    }

    @Override
    public HasValue<TreeSelectionModel> getTreeSelectionModel() {
	return searchTreeSelectionInterface;
    }
    
    void render() {
	int screenWith = uiContext.getAppContext().getBean(Screen.class).getWidth();
	
	int columnCount = searchtree.getColumnCount();
	for (int i = 0; i < columnCount; i++) {
	    final TableColumn col = searchtree.getColumn(i);
	    col.setPreferredWidth(screenWith / columnCount);
	}
	searchtree.expandAll();
	pack();
	setVisible(true);
    }

    @Override
    public C getViewContainer() {
	return (C) this;
    }

}
