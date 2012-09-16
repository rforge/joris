package org.rosuda.ui.mmi;

import org.rosuda.ui.core.mvc.HasValue.ValueChangeListener;
import org.rosuda.ui.core.mvc.MVP;
import org.rosuda.ui.core.mvc.MessageBus;
import org.rosuda.visualizer.NodeTreeSelection;

public class MMIToolPresenter<T, C> implements MVP.Presenter<MMIToolModel<T>, MMIToolView<T, C>> {

    @Override
    public void bind(final MMIToolModel<T> model, final MMIToolView<T, C> view,final MessageBus mb) {
	view.getUniqueStructureTree().setValue(model.getUniqueStructure());
	view.getMMITable().setValue(model.getTableModel());
	view.getUniqueStructureSelection().addChangeListener(new ValueChangeListener<NodeTreeSelection>() {	    
	    @Override
	    public void onValueChange(NodeTreeSelection newValue) {
		model.getTableModel().updateSelection(newValue);
	    }
	});
	
//	view.getSynchronizeTreeToTable().addClickListener(new ClickListener() {
//	    @Override
//	    public void onClick(ClickEvent event) {
//		final TableModel mmiTableModel = new Create
//		//get actual selection
//		model.getUniqueStructure().get XX
//		TableModel updatedTableModel = null; //TODO calculate values from table, model
//		view.getMMITable().setValue(updatedTableModel);
//	    }
//	});
	view.show();
	
    }

    @Override
    public void unbind(MMIToolModel<T> model, MMIToolView<T, C> view, MessageBus mb) {
	// TODO Auto-generated method stub
	
    }
  
}
