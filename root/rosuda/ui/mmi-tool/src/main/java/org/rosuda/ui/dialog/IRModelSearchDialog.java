package org.rosuda.ui.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.TableColumn;

import org.jdesktop.swingx.JXTreeTable;
import org.rosuda.ui.MainFrame;
import org.rosuda.ui.context.UIContext;
import org.rosuda.ui.core.mvc.MessageBus;
import org.rosuda.ui.core.mvc.Screen;
import org.rosuda.ui.event.ModelSearchEvent;
import org.rosuda.ui.listener.WindowCloseListener;
import org.rosuda.ui.model.SearchTreeModel;
import org.swixml.SwingEngine;

public class IRModelSearchDialog extends JDialog {

	private static final long serialVersionUID = -4983090346927958415L;
	private final SearchTreeModel searchTreeModel = new SearchTreeModel();
	private JXTreeTable searchTree = new JXTreeTable();
	private JPanel panel;
	
	private JMenuItem close;
	private JMenuItem search;
	private JButton searchButton;
	private Screen screen;

	public IRModelSearchDialog(final UIContext context) throws Exception {
		super(context.getUIFrame(), ModalityType.MODELESS);
		final InputStream rsc = MainFrame.class.getResourceAsStream("/gui/dialog/ModelSearchDialog.xml");
		final BufferedReader reader = new BufferedReader(new InputStreamReader(
				rsc));
		new SwingEngine<JDialog>(this).render(reader);
		reader.close();
		final MessageBus messageBus = context.getAppContext().getBean(MessageBus.class);
		close.addActionListener(new WindowCloseListener(messageBus, this));
		search.addActionListener(new ModelSearchActionListener(messageBus));
		searchButton.addActionListener(new ModelSearchActionListener(messageBus));
		this.screen = context.getAppContext().getBean(Screen.class);
	}
	
	public IRModelSearchDialog showNewDialog() {
		JScrollPane scrollPane = new JScrollPane(searchTree,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		panel.add(scrollPane);
		searchTree.setTreeTableModel(searchTreeModel);
		int screenWith = screen.getWidth();
		for (int i=0;i<searchTreeModel.getColumnCount();i++) { 
			final TableColumn col = searchTree.getColumn(i);
			col.setPreferredWidth(screenWith/searchTreeModel.getColumnCount());
		}
		searchTree.expandAll();
		pack();
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		setVisible(true);
		return this;
	}
	
	private class ModelSearchActionListener implements ActionListener {

		private final MessageBus messageBus;
		
		private ModelSearchActionListener(final MessageBus messageBus) {
			this.messageBus = messageBus;
		}
		
		@Override
		public void actionPerformed(final ActionEvent event) {
			messageBus.fireEvent(new ModelSearchEvent(searchTreeModel.getConstraints()));
		}
		
	}
}
