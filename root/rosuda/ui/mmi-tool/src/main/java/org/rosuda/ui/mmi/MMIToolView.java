package org.rosuda.ui.mmi;

import javax.swing.table.TableModel;

import org.rosuda.ui.core.mvc.HasClickable;
import org.rosuda.ui.core.mvc.HasValue;
import org.rosuda.ui.core.mvc.MVP;
import org.rosuda.visualizer.NodeTreeModel;
import org.rosuda.visualizer.NodeTreeSelection;

public interface MMIToolView<T, C> extends MVP.View<C> {

    HasValue<NodeTreeModel<T>> getUniqueStructureTree();
    
    HasValue<NodeTreeSelection> getUniqueStructureSelection();
    
    HasValue<TableModel> getMMITable();
    
    HasClickable getSynchronizeTreeToTable();

}
