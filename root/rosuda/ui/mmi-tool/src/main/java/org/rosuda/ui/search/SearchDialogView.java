package org.rosuda.ui.search;

import org.rosuda.ui.core.mvc.HasClickable;
import org.rosuda.ui.core.mvc.HasValue;
import org.rosuda.ui.core.mvc.MVP;

public interface SearchDialogView<C> extends MVP.View<C> {
    
    HasClickable getCloseButton();
    HasClickable getSearchButton();
    HasValue<SearchTreeModel> getTree();
    
}
