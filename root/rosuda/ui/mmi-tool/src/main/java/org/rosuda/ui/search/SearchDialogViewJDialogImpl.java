package org.rosuda.ui.search;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.table.TableColumn;

import org.jdesktop.swingx.JXTreeTable;
import org.rosuda.mvc.swing.JButtonHasClickable;
import org.rosuda.ui.SwingLayoutProcessor;
import org.rosuda.ui.context.UIContext;
import org.rosuda.ui.core.mvc.HasClickable;
import org.rosuda.ui.core.mvc.HasValue;
import org.rosuda.ui.core.mvc.Screen;

public class SearchDialogViewJDialogImpl<C extends JDialog> extends JDialog implements SearchDialogView<C> {

    /**
     * 
     */
    private static final long serialVersionUID = 247567699446989556L;

    public JXTreeTable searchtree = new JXTreeTable();
 
    private JMenuItem close = new JMenuItem();
    public JButton searchbutton = new JButton();

    private final HasClickable closeButtonInterface;
    private final HasClickable searchButtonInterface;
    private final HasValue<SearchTreeModel> searchTreeInterface;

    private final UIContext uiContext;

    public SearchDialogViewJDialogImpl(final UIContext context) throws Exception {
	super(context.getUIFrame(), ModalityType.MODELESS);
	this.uiContext = context;
	SwingLayoutProcessor.processLayout(this, "/gui/dialog/ModelSearchDialog.xml");
	
	searchButtonInterface = new JButtonHasClickable(searchbutton);
	closeButtonInterface = new JButtonHasClickable(close);
	searchTreeInterface = new HasValue<SearchTreeModel>() {

	    @Override
	    public SearchTreeModel getValue() {
		return (SearchTreeModel) searchtree.getTreeTableModel();
	    }

	    @Override
	    public void setValue(SearchTreeModel value) {
		searchtree.setTreeTableModel(value);
	    }

	    @Override
	    public void addChangeListener(org.rosuda.ui.core.mvc.HasValue.ValueChangeListener<SearchTreeModel> listener) {
		// TODO Auto-generated method stub		
	    }

	    @Override
	    public void removeChangeListener(org.rosuda.ui.core.mvc.HasValue.ValueChangeListener<SearchTreeModel> listener) {
		// TODO Auto-generated method stub
	    }
	    
	};
	//render
	
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
    public HasValue<SearchTreeModel> getTree() {
	return searchTreeInterface;
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
