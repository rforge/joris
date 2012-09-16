package org.rosuda.ui.core.mvc;


public abstract class AbstractTestView<C> implements MVP.View<C> {

    private final C container;
    
    private boolean enabled = true;
    private boolean visible = false;
    
    public AbstractTestView(final C container) {
	this.container = container;
    }
    
    @Override
    public final void disable() {
	this.enabled = false;
    }

    @Override
    public final void enable() {
	this.enabled = true;
    }

    @Override
    public final void show() {
	this.visible = true;
    }

    @Override
    public final void hide() {
	this.visible = false;
    }

    @Override
    public C getViewContainer() {
	return container;
    }

    public boolean isEnabled() {
	return enabled;
    }
    
    public boolean isVisible() {
	return visible;
    }
}
