package org.rosuda.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import javax.swing.AbstractButton;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.reflections.Reflections;
import org.rosuda.ui.context.AwareTypeUtil;
import org.rosuda.ui.context.UIContext;
import org.rosuda.ui.context.Aware;
import org.rosuda.ui.core.mvc.MessageBus;
import org.rosuda.ui.core.mvc.MessageBus.Event;
import org.rosuda.ui.core.mvc.MessageBus.EventListener;

public class UIProcessor {

	private static final Log LOG = LogFactory.getLog(UIProcessor.class);

	public void bindEvents(final MessageBus messageBus, final Object uiObject, final UIContext context) {
	    final Set<Class<? extends MessageBus.Event>> eventTypes = new HashSet<Class<? extends Event>>();
	    for (final Field field: uiObject.getClass().getFields()) {
		if (AbstractButton.class.isAssignableFrom(field.getType())) {
		    final Class<? extends MessageBus.Event> eventType = createActionForButton(field, messageBus, uiObject, context);
		    if (eventType != null) {
			eventTypes.add(eventType);
		    }
		}
	    }
	    final Reflections reflections = new Reflections("org.rosuda.ui.event");
	    eventTypes.addAll(reflections.getSubTypesOf(MessageBus.Event.class));
	    for (final Class<? extends MessageBus.Event> eventType : eventTypes) {
		registerEventHandler(eventType, messageBus, context);
	    }
	}
	
	private Class<? extends MessageBus.Event> createActionForButton(final Field button, final MessageBus messageBus, final Object uiObject, final UIContext context) {
		final String actionClassName = 
			new StringBuilder("org.rosuda.ui.event.")
				.append(button.getName().substring(0,1).toUpperCase())
				.append(button.getName().substring(1, button.getName().length()))
				.append("Event").toString();
		try {
			final Class<?> actionEventClass = Class.forName(actionClassName);
			final MessageBus.Event messageBusEvent = (Event) actionEventClass.newInstance();
			final AbstractButton abstractButton = (AbstractButton) button.get(uiObject);
			abstractButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent event) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							LOG.debug("fireEvent:"+messageBusEvent);
							messageBus.fireEvent(messageBusEvent);							
						}
					}).run();
				}
			});
			return messageBusEvent.getClass();			
		} catch (final Exception e) {
			LOG.warn("no Action on AbstractButton "+button.getName(),e);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private void registerEventHandler(final Class<? extends MessageBus.Event> eventType, final MessageBus messageBus, final UIContext context) {
		final String eventName = eventType.getSimpleName();
		final String eventClassName = 
			new StringBuilder("org.rosuda.ui.handler.")
				.append(eventName)
				.append("Handler").toString();
		try {
			final Class<?> eventHandlerClassName = Class.forName(eventClassName);
			final MessageBus.EventListener<?> handler;
			if (context.getAppContext().getBeanNamesForType(eventHandlerClassName).length == 1) {
				handler = (EventListener<?>) context.getAppContext().getBean(eventHandlerClassName);
				LOG.debug("fetch handler "+handler+" from spring context");
			} else {
				handler = (EventListener<?>) eventHandlerClassName.newInstance();
				LOG.debug("created handler "+handler+" by reflection");
			}
			messageBus.registerListener(handler);
			if (Aware.class.isAssignableFrom(eventHandlerClassName)) {
			    final Aware<?> aware = (Aware<?>) handler;
LOG.error("*** binding handler :"+aware);			  
			    if (UIContext.class.isAssignableFrom(AwareTypeUtil.getType(aware))) {
				((Aware<UIContext>)aware).setContext(context);
			    } 
			    if (MessageBus.class.isAssignableFrom(AwareTypeUtil.getType(aware))) {
				((Aware<MessageBus>)aware).setContext(messageBus);
			    }
			}
		} catch (final Exception e) {
			LOG.warn("no EventHandler for "+eventType,e);
		}
	}

}
