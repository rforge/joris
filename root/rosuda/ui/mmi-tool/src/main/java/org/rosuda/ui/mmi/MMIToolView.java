package org.rosuda.ui.mmi;

import org.rosuda.irconnect.IREXP;
import org.rosuda.ui.core.mvc.HasValue;
import org.rosuda.ui.core.mvc.MVP;
import org.rosuda.visualizer.NodeTreeModel;

public interface MMIToolView<C> extends MVP.View<C> {

    HasValue<NodeTreeModel<IREXP>> getUniqueStructureTree();
}
