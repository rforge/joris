package org.rosuda.ui.core.mvc;

import java.util.Set;

public interface HasClickable {


	public interface ClickEvent {
		public enum Modifier {
			ALT, SHIFT, CTRL, META
		}
		public int getClickCount();	
		public int getButton();
		public Set<Modifier> getModifiers();
	}
		
	public void addClickListener(final ClickListener listener);
	
	public void removeClickListener(final ClickListener listener);
	
	public interface ClickListener {
		public void onClick(final ClickEvent event);
	}
}
