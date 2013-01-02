package org.rosuda.ui.mmi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListSelectionModel;
import javax.swing.event.ListDataListener;

import org.rosuda.irconnect.IREXP;
import org.rosuda.mvc.swing.TypedDynamicListModel;
import org.rosuda.type.Node;
import org.rosuda.ui.dialog.RootNodeWrapper;
import org.rosuda.ui.test.ModelInitializer;
import org.rosuda.visualizer.NodeTreeModel;

public class MMIToolTestModelData extends ModelInitializer<MMIToolModel<IREXP>> {

    private List<Node<IREXP>> data;
    private MMIDynamicTableModel<IREXP> tableModel;
    private TypedDynamicListModel<String> listModel;

    public MMIToolTestModelData() throws IOException, ClassNotFoundException {
	data = new ArrayList<Node<IREXP>>();
	for (int i = 1; i <= 8; i++) {
	    final String rscName = "/models/airquality-" + i + ".rObj";
	    data.add(loadResource(rscName));
	}
	final ArrayList<String> valueList = new ArrayList<String>();
	tableModel = new MMIDynamicTableModel<IREXP>(data);
	listModel = new TypedDynamicListModel<String>() {

	    @Override
	    public int getSize() {
		return valueList.size();
	    }

	    @Override
	    public Object getElementAt(int index) {
		return valueList.get(index);
	    }

	    @Override
	    public void add(String value) {
		valueList.add(value);
	    }

	    @Override
	    public String remove(String value) {
		if (valueList.remove(value)) {
		    return value;
		}
		return null;
	    }

	    @Override
	    public void addListDataListener(ListDataListener l) {
	    }

	    @Override
	    public void removeListDataListener(ListDataListener l) {
	    }

	    @Override
	    public String at(int index) {
		return valueList.get(index);
	    }

	};
    }

    protected Node<IREXP> dataAt(final int index) {
	return data.get(index);
    }

    @Override
    protected void initModel(MMIToolModel<IREXP> model) {
	model.setTableModel(tableModel);
	model.setUniqueStructure(new NodeTreeModel<IREXP>(new RootNodeWrapper<IREXP>(null, data)));
	model.setExpressionListModel(listModel);
	model.setExpressionListSelectionModel(new DefaultListSelectionModel());
    }

    @SuppressWarnings("unchecked")
    private static Node<IREXP> loadResource(final String resourceName) throws IOException, ClassNotFoundException {
	final ObjectInputStream ois = new ObjectInputStream(MMIToolTest.class.getResourceAsStream(resourceName));
	final Object rootNode = ois.readObject();
	ois.close();
	return (Node<IREXP>) rootNode;
    }
}
