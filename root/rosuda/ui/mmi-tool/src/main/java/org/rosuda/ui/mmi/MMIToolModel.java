package org.rosuda.ui.mmi;

import org.rosuda.irconnect.IREXP;
import org.rosuda.ui.core.mvc.MVP;
import org.rosuda.visualizer.NodeTreeModel;

public class MMIToolModel  implements MVP.Model{

    private NodeTreeModel<IREXP> uniqueStructure;
    
    NodeTreeModel<IREXP> getUniqueStructure() {
	return uniqueStructure;
    }
    
    void setUniqueStructure(NodeTreeModel<IREXP> uniqueStructure) {
	this.uniqueStructure = uniqueStructure;
    }
}
