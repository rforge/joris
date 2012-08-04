package org.rosuda.ui.core.mvc;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public interface MessageBus {

    public interface Event {
    }

    public abstract class EventListener<E extends Event> {
	public abstract void onEvent(final E event);

	@SuppressWarnings("unchecked")
	void callForEvent(final Event event) {
	    onEvent((E) event);
	}
    }

    public void fireEvent(final Event event);

    public void registerListener(final EventListener<?> c);

    public void removeListener(final EventListener<?> c);

    public static final MessageBus INSTANCE = new Impl();

    public static class Impl implements MessageBus {

	private static final Log LOG = LogFactory.getLog(Impl.class);
	private boolean asynchMode = true;
	
	protected void setAsynchMode(boolean asynchMode) {
	    this.asynchMode = asynchMode;
	}

	private final Map<Class<? extends Event>, List<EventListener<? extends Event>>> eventListeners = new HashMap<Class<? extends Event>, List<EventListener<? extends Event>>>();

	public void fireEvent(final Event event) {
	    if (event == null)
		return;
	    if (asynchMode) {
		new Thread(new Runnable() {

		    @Override
		    public void run() {
			processEvent(event);
		    }

		}).start();
	    } else {
		processEvent(event);
	    }
	}

	private void processEvent(final Event event) {
	    final Iterable<EventListener<?>> notified;
	    if (eventListeners.containsKey(event.getClass())) {
		notified = eventListeners.get(event.getClass());
	    } else if (eventListeners.containsKey(MessageBus.Event.class)) {
		notified = eventListeners.get(MessageBus.Event.class);
	    } else {
		notified = Collections.emptyList();
	    }
	    for (final EventListener<?> listener : notified) {
		listener.callForEvent(event);
	    }
	}

	@SuppressWarnings("unchecked")
	private Class<? extends Event> determineEventClass(final EventListener<?> c) {
	    // extract Event type
	    Class<? extends Event> registerClass = Event.class;
	    try {
		for (Method meth : c.getClass().getMethods()) {
		    if ("onEvent".equals(meth.getName()) && !Event.class.equals(meth.getParameterTypes()[0])) {
			registerClass = (Class<? extends Event>) meth.getParameterTypes()[0];
		    }
		}
	    } catch (final Exception x) {
		throw new RuntimeException(x);
	    }
	    return registerClass;
	}

	public void registerListener(final EventListener<?> c) {
	    final Class<? extends Event> registerClass = determineEventClass(c);
	    final List<EventListener<?>> listenerList;
	    LOG.debug("registering listener "+c);
	    if (eventListeners.containsKey(registerClass)) {
		listenerList = eventListeners.get(registerClass);
	    } else {
		listenerList = new ArrayList<EventListener<?>>();
		eventListeners.put(registerClass, listenerList);
	    }
	    listenerList.add(c);
	}

	public void removeListener(final EventListener<?> c) {
	    LOG.debug("removing listener "+c);
	    final Class<? extends Event> registerClass = determineEventClass(c);
	    if (eventListeners.containsKey(registerClass)) {
		eventListeners.get(registerClass).remove(c);
	    }
	}

    }
}
