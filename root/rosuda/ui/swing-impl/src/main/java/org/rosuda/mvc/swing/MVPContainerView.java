package org.rosuda.mvc.swing;

import java.awt.Component;
import java.awt.Container;

import org.rosuda.ui.core.mvc.MVP;

public class MVPContainerView<T extends Container> implements MVP.View<T>{

	private T container;
	
	public MVPContainerView(final T container) {
		this.container = container;
	}
	
	private void setEnabled(final boolean enabled) {
		container.setEnabled(enabled);
		for (final Component c: container.getComponents()) {
			c.setEnabled(enabled);
		}
	}
	
	private void setVisible(final boolean visible) {
		container.setVisible(visible);
		for (final Component c: container.getComponents()) {
			c.setVisible(visible);
		}
	}
	
	@Override
	public void disable() {
		setEnabled(false);
	}

	@Override
	public void enable() {
		setEnabled(true);
	}

	@Override
	public void show() {
		setVisible(true);
	}

	@Override
	public void hide() {
		setVisible(false);
	}

	@Override
	public T getViewContainer() {
		return container;
	}

}
