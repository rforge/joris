package org.rosuda.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import javax.swing.AbstractButton;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.rosuda.ui.context.UIContext;
import org.rosuda.ui.core.mvc.MessageBus;
import org.rosuda.ui.core.mvc.MessageBus.Event;
import org.rosuda.ui.core.mvc.MessageBus.EventListener;

public class UIProcessor {

	private static final Log LOG = LogFactory.getLog(UIProcessor.class);
	
	public void bindEvents(final MessageBus messageBus, final Object uiObject, final UIContext context) {
		for (final Field field: uiObject.getClass().getDeclaredFields()) {
			if (AbstractButton.class.isAssignableFrom(field.getType())) {
				createActionForButton(field, messageBus, uiObject, context);
			}
		}	
	}
	
	private void createActionForButton(final Field button, final MessageBus messageBus, final Object uiObject, final UIContext context) {
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
							messageBus.fireEvent(messageBusEvent);							
						}
					}).run();
				}
			});
			registerEventHandler(messageBusEvent, messageBus, context);
			
		} catch (final Exception e) {
			LOG.warn("no Action on AbstractButton "+button.getName(),e);
		}
	}
	
	private void registerEventHandler(final MessageBus.Event event, final MessageBus messageBus, final UIContext context) {
		final String eventName = event.getClass().getSimpleName();
		final String eventClassName = 
			new StringBuilder("org.rosuda.ui.handler.")
				.append(eventName)
				.append("Handler").toString();
		try {
			final Class<?> eventClass = Class.forName(eventClassName);
			final Constructor<?> eventConstructor = eventClass.getConstructor(UIContext.class);
			final MessageBus.EventListener<?> handler = (EventListener<?>) eventConstructor.newInstance(context);
			messageBus.registerListener(handler);
		} catch (final Exception e) {
			LOG.warn("no EventHandler for "+event,e);
		}
	}

}
