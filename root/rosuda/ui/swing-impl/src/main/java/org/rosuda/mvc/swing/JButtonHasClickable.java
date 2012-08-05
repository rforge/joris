package org.rosuda.mvc.swing;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractButton;
import javax.swing.JButton;

import org.rosuda.ui.core.mvc.HasClickable;

public class JButtonHasClickable implements HasClickable {

	private final AbstractButton button;
	private final List<HasClickable.ClickListener> listeners = new ArrayList<HasClickable.ClickListener>();
	
	public JButtonHasClickable() {
	    this(new JButton());
	}
	
	public JButtonHasClickable(final AbstractButton button) {
		this.button = button;
		this.button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent me) {
				fireMouseEvent(me);
			}
		
		});
	}
	
	public AbstractButton getJButton() {
		return button;
	}

	private void fireMouseEvent(final MouseEvent event) {
		final ClickEvent clickEvent = new JClickEvent(event);
		for (final HasClickable.ClickListener listener: listeners) {
			listener.onClick(clickEvent);
		}
	}
	
	class JClickEvent implements ClickEvent {
		private final MouseEvent me;
		private Set<Modifier> modifiers;
		
		JClickEvent(final MouseEvent me) {
			this.me = me;
		}

		public int getClickCount() {
			return me.getClickCount();
		}

		public int getButton() {
			return me.getButton();
		}

		public Set<Modifier> getModifiers() {
			if (modifiers != null)
				return modifiers;
			synchronized (this) {
				this.modifiers = new HashSet<HasClickable.ClickEvent.Modifier>();
				if (me.isAltDown())
					this.modifiers.add(Modifier.ALT);
				if (me.isShiftDown())
					this.modifiers.add(Modifier.SHIFT);
				if (me.isMetaDown())
					this.modifiers.add(Modifier.META);
				if (me.isControlDown())
					this.modifiers.add(Modifier.CTRL);
			}
			return modifiers;
		}
	}

	public void addClickListener(final ClickListener listener) {
		this.listeners.add(listener);
	}



	public void removeClickListener(final ClickListener listener) {
		this.listeners.remove(listener);
	}

}
