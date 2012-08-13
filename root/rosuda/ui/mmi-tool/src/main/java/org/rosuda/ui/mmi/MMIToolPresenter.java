package org.rosuda.ui.mmi;

import org.rosuda.ui.core.mvc.MVP;
import org.rosuda.ui.core.mvc.MessageBus;

public class MMIToolPresenter implements MVP.Presenter<MMIToolModel, MMIToolView<?>> {

    @Override
    public void bind(MMIToolModel model, MMIToolView<?> view, MessageBus mb) {
	view.getUniqueStructureTree().setValue(model.getUniqueStructure());
	view.show();
	
    }

    @Override
    public void unbind(MMIToolModel model, MMIToolView<?> view, MessageBus mb) {
	// TODO Auto-generated method stub
	
    }
  
}
