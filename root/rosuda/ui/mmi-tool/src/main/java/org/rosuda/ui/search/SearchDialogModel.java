package org.rosuda.ui.search;

import org.rosuda.ui.core.mvc.MVP;


public class SearchDialogModel implements MVP.Model{

    private final SearchTreeModel searchTreeModel = new SearchTreeModel();
    
    SearchTreeModel getSearchTreeModel() {
	return searchTreeModel;
    }
    
}
