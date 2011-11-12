package org.rosuda.ui.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rosuda.irconnect.IRConnection;
import org.rosuda.irconnect.IREXP;
import org.rosuda.type.Node;
import org.rosuda.ui.context.UIContext;
import org.rosuda.ui.core.mvc.MessageBus;
import org.rosuda.ui.dialog.IREXPModelSelectionDialog;
import org.rosuda.ui.event.ScanWorkspaceEvent;
import org.rosuda.ui.work.ReadAllObjectsFromRConnection;
import org.rosuda.ui.work.WrapIREXPAsNode;

import com.google.common.base.Function;
import com.google.common.base.Functions;

public class ScanWorkspaceEventHandler extends
		MessageBus.EventListener<ScanWorkspaceEvent> {

	private static final Log LOG = LogFactory.getLog(ScanWorkspaceEventHandler.class);
	
	private UIContext context;

	public ScanWorkspaceEventHandler(final UIContext eventContext) {
		this.context = eventContext;
	}

	@Override
	public void onEvent(final ScanWorkspaceEvent event) {
		long tick = System.currentTimeMillis();
		final IRConnection connection = context.getAppContext().getBean("managedConnection", IRConnection.class);
		final Function<IREXP, Node<IREXP>> filterTransformation = new WrapIREXPAsNode();
		final Function<IRConnection, Node<IREXP>> transformation = Functions.compose(filterTransformation, ReadAllObjectsFromRConnection.getInstance());
		final Node<IREXP> environmentNode = transformation.apply(connection);
		final long mark1 = System.currentTimeMillis() - tick;
		LOG.info("evaluated workspace in "+mark1+" ms.");
		try {
			new IREXPModelSelectionDialog(context).showWithNode(environmentNode);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
//		tick = System.currentTimeMillis();
//		
//		final Visualizer<IREXP> visualizer = new Visualizer<IREXP>(environmentNode);
//		//TODO with ?swixml?
//		final JDialog selectionDialog = new JDialog(context.getUIFrame());
//		final JScrollPane innerPanel = new JScrollPane(visualizer);
//		selectionDialog.getContentPane().add(innerPanel);
//		selectionDialog.pack();
//		selectionDialog.setVisible(true);
	}
}
