package org.rosuda.ui.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import org.rosuda.irconnect.IREXP;
import org.rosuda.type.Node;
import org.rosuda.ui.MainFrame;
import org.rosuda.ui.UIProcessor;
import org.rosuda.ui.context.UIContext;
import org.rosuda.ui.core.mvc.MessageBus;
import org.rosuda.ui.event.StoreSelectionEvent;
import org.rosuda.ui.listener.WindowCloseListener;
import org.rosuda.visualizer.Visualizer;
import org.swixml.SwingEngine;

public class IREXPModelSelectionDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7248740770850757226L;
	private JPanel visualizerPanel;
	private Visualizer<IREXP> visualizer;
	
	private JMenuItem close;
	private JMenuItem storeSelection;
	
	public IREXPModelSelectionDialog(final UIContext context) throws Exception {
		super(context.getUIFrame());
		final InputStream rsc = MainFrame.class.getResourceAsStream("/gui/dialog/ModelSelectionDialog.xml");
		final BufferedReader reader = new BufferedReader(new InputStreamReader(
				rsc));
		new SwingEngine<JDialog>(this).render(reader);
		reader.close();
		final MessageBus messageBus = context.getAppContext().getBean(MessageBus.class);
		//TODO MVP
		//close.addActionListener(new WindowCloseListener(messageBus, this));
		storeSelection.addActionListener(new StoreSelectionActionListener(messageBus));
	}
	
	public IREXPModelSelectionDialog showWithNode(final Node<IREXP> environmentNode) {
		visualizerPanel.removeAll();
		this.visualizer = new Visualizer<IREXP>(environmentNode);
		visualizerPanel.add(visualizer);
		setVisible(true);
		return this;
	}
	
	private class StoreSelectionActionListener implements ActionListener {

		private final MessageBus messageBus;
		
		private StoreSelectionActionListener(final MessageBus messageBus) {
			this.messageBus = messageBus;
		}
		
		@Override
		public void actionPerformed(final ActionEvent event) {
			messageBus.fireEvent(new StoreSelectionEvent(visualizer.getSelectedNodes()));
		}
		
	}
}
