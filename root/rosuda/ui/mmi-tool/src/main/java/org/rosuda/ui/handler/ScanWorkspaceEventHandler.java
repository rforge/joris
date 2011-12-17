package org.rosuda.ui.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rosuda.irconnect.IRConnection;
import org.rosuda.irconnect.IREXP;
import org.rosuda.type.Node;
import org.rosuda.ui.context.UIContext;
import org.rosuda.ui.context.UIContextAware;
import org.rosuda.ui.core.mvc.MessageBus;
import org.rosuda.ui.dialog.IREXPModelSelectionDialog;
import org.rosuda.ui.event.ScanWorkspaceEvent;
import org.rosuda.ui.work.ReadAllObjectsFromRConnection;
import org.rosuda.ui.work.WrapIREXPAsNode;

import com.google.common.base.Function;
import com.google.common.base.Functions;

public class ScanWorkspaceEventHandler extends
		MessageBus.EventListener<ScanWorkspaceEvent> implements UIContextAware{

	private static final Log LOG = LogFactory.getLog(ScanWorkspaceEventHandler.class);
	
	private UIContext context;

	public ScanWorkspaceEventHandler() {
	}

	@Override
	public void onEvent(final ScanWorkspaceEvent event) {
		long tick = System.currentTimeMillis();
		final IRConnection connection = context.getAppContext().getBean("managedConnection", IRConnection.class);
		final Function<IREXP, Node<IREXP>> filterTransformation = context.getAppContext().getBean(WrapIREXPAsNode.class);
		final Function<IRConnection, Node<IREXP>> transformation = Functions.compose(filterTransformation, ReadAllObjectsFromRConnection.getInstance());
		final Node<IREXP> environmentNode = transformation.apply(connection);
		final long mark1 = System.currentTimeMillis() - tick;
		LOG.info("evaluated workspace in "+mark1+" ms.");
		try {
			new IREXPModelSelectionDialog(context).showWithNode(environmentNode);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setUIContext(final UIContext context) {
		this.context = context;
	}
}
