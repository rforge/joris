package org.rosuda.ui.context;

import java.awt.Window;

import org.springframework.context.ApplicationContext;

public interface UIContext {

	ApplicationContext getAppContext();
	
	Window getUIFrame();

}
