package org.rosuda.mapper.filter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.rosuda.type.Node;
import org.rosuda.type.Value;

public abstract class NodeValueFilter<T> {

private final Set<EventListener<T>> listeners = new HashSet<EventListener<T>>();
	
	/**
	 * accept to process object "object" under parent-node "node"
	 * @param object
	 * @param node
	 * @return
	 */
	public final boolean accept(final Node.Builder<T> parent, final Value newValue) {
		boolean accepted = maySetValue(parent, newValue);
		fireEvent(new EventImpl<T>(accepted, parent));
		return accepted;
	}
	protected abstract boolean maySetValue(final Node.Builder<T> parent, final Value newValue);

	protected void fireEvent(final Event<T> event) {
		for (EventListener<T> listener: listeners) {
			listener.triggered(event);
		}
	}
	
	public void addListener(final EventListener<T> listener) {
		listeners.add(listener);
	}
	
	public void removeListener(final EventListener<T> listener) {
		listeners.remove(listener);
	}
	
	public Iterable<EventListener<T>> getListeners() {
		return new ArrayList<EventListener<T>>(listeners);
	}
		
	public interface Event<T> {
		/**
		 * positive or negative event
		 * @return
		 */
		public boolean wasAccepted();
		/**
		 * optional description
		 * @return
		 */
		public String getDescription();
		/**
		 * the node that the event was produced on
		 * @return
		 */
		public Node.Builder<T> getNode();
	}
	
	private static class EventImpl<T> implements Event<T> {
		private final boolean accepted;
		private final Node.Builder<T> node;
		
		private EventImpl(final boolean accepted, final Node.Builder<T> node) {
			this.accepted = accepted;
			this.node = node;
		}
		
		public boolean wasAccepted() {
			return accepted;
		}

		public String getDescription() {
			return null;
		}

		public Node.Builder<T> getNode() {
			return node;
		}		
	}

	public interface EventListener<T> {
		void triggered(final Event<T> event);
	}
}
