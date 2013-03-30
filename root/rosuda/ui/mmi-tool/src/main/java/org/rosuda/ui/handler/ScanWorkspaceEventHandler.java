package org.rosuda.ui.handler;

import org.rosuda.irconnect.IRConnection;
import org.rosuda.irconnect.IREXP;
import org.rosuda.type.Node;
import org.rosuda.ui.context.Aware;
import org.rosuda.ui.context.UIContext;
import org.rosuda.ui.core.mvc.MessageBus;
import org.rosuda.ui.dialog.IREXPModelSelectionDialog;
import org.rosuda.ui.event.ScanWorkspaceEvent;
import org.rosuda.ui.work.ReadAllObjectsFromRConnection;
import org.rosuda.ui.work.WrapIREXPAsNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.google.common.base.Function;
import com.google.common.base.Functions;

@Component
public class ScanWorkspaceEventHandler extends MessageBus.EventListener<ScanWorkspaceEvent> implements Aware<UIContext> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScanWorkspaceEventHandler.class);

    @Autowired
    @Qualifier("managedConnection")
    private IRConnection connection;
    @Autowired
    private WrapIREXPAsNode filterTransformation;

    private UIContext context;

    public ScanWorkspaceEventHandler() {
    }

    @Override
    public void onEvent(final ScanWorkspaceEvent event) {
        long tick = System.currentTimeMillis();
        final Function<IRConnection, Node<IREXP>> transformation = Functions.compose(filterTransformation,
                ReadAllObjectsFromRConnection.getInstance());
        final Node<IREXP> environmentNode = transformation.apply(connection);
        final long mark1 = System.currentTimeMillis() - tick;
        LOGGER.info("evaluated workspace in " + mark1 + " ms.");
        try {
            new IREXPModelSelectionDialog(context).showWithNode(environmentNode);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setContext(final UIContext context) {
        this.context = context;
    }
}
