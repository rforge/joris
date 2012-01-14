package org.rosuda.ui.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rosuda.graph.service.GraphService;
import org.rosuda.irconnect.IRConnection;
import org.rosuda.irconnect.IREXP;
import org.rosuda.type.Node;
import org.rosuda.ui.context.UIContext;
import org.rosuda.ui.context.UIContextAware;
import org.rosuda.ui.core.mvc.MessageBus;
import org.rosuda.ui.dialog.IREXPModelSelectionDialog;
import org.rosuda.ui.event.ScanWorkspaceEvent;
import org.rosuda.ui.event.SearchDataEvent;
import org.rosuda.ui.work.ReadAllObjectsFromRConnection;
import org.rosuda.ui.work.WrapIREXPAsNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Function;
import com.google.common.base.Functions;

@Component
public class SearchDataEventHandler extends
		MessageBus.EventListener<SearchDataEvent> implements UIContextAware{

	private static final Log LOG = LogFactory.getLog(SearchDataEventHandler.class);
	
	private UIContext context;

	@Autowired
	private GraphService<IREXP> dataService;
	
	public SearchDataEventHandler() {
	}

	@Override
	public void onEvent(final SearchDataEvent event) {
		dataService.find(null);
	}

	@Override
	public void setUIContext(final UIContext context) {
		this.context = context;
	}
}
