package org.rosuda.ui.handler;

import org.rosuda.ui.context.Aware;
import org.rosuda.ui.context.UIContext;
import org.rosuda.ui.core.mvc.MessageBus;
import org.rosuda.ui.event.QuitEvent;
import org.rosuda.util.process.ProcessStopper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuitEventHandler extends MessageBus.EventListener<QuitEvent> implements Aware<UIContext> {

    private UIContext context;
    final Logger logger = LoggerFactory.getLogger(QuitEventHandler.class);

    @Override
    public void onEvent(final QuitEvent event) {
	context.getUIFrame().setVisible(false);
	for (final String beanName : context.getAppContext().getBeanDefinitionNames()) {
	    final Object bean = context.getAppContext().getBean(beanName);
	    if (bean instanceof ProcessStopper<?>) {
		final ProcessStopper<?> stopper = (ProcessStopper<?>) bean;
		logger.info("stopping " + stopper);
		try {
		stopper.stop();
		} catch (final Exception x) {
		    logger.error("stopping "+bean, x);
		}
	    } else if (bean instanceof MessageBus) {
		final MessageBus msb = (MessageBus) bean;
		msb.shutdown();
	    }
	}
	context.getUIFrame().dispose();
	System.exit(0);
    }

    @Override
    public void setContext(final UIContext context) {
	this.context = context;
    }

}
