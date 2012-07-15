package org.rosuda.ui.main;

import org.rosuda.ui.core.mvc.MVP;
import org.springframework.context.ApplicationContext;

public class MainModel implements MVP.Model {

	private final ApplicationContext context;

	public MainModel(ApplicationContext context) {
		this.context = context;
	}

	public ApplicationContext getContext() {
		return context;
	}
	//evtl R workspace (l8er)
}
