package org.rosuda.ui.main;

import java.awt.Container;

import javax.swing.JEditorPane;
import javax.swing.text.JTextComponent;
import javax.swing.text.html.HTMLDocument;

import org.rosuda.mvc.swing.DocumentHasValue;
import org.rosuda.mvc.swing.DocumentValueAdapter;
import org.rosuda.mvc.swing.JTextComponentHasKeyEvent;
import org.rosuda.mvc.swing.MVPContainerView;
import org.rosuda.ui.core.mvc.HasKeyEvent;
import org.rosuda.ui.core.mvc.HasValue;
import org.rosuda.ui.core.mvc.impl.AbstractHasValue;

public class MainViewContainerImpl<C extends Container> extends MVPContainerView<C> implements MainView<C> {

    private final HasValue<String> inputValue;
    private final HasKeyEvent inputEvent;
    private final HasValue<HTMLDocument> protocol;

    public MainViewContainerImpl(final C container, final JTextComponent textComponent, final JEditorPane protocolComponent) throws Exception {
	super(container);
	this.inputEvent = new JTextComponentHasKeyEvent(textComponent);
	this.inputValue = new DocumentHasValue<String>(textComponent.getDocument(), new DocumentValueAdapter.String(textComponent.getDocument()));
	this.protocol = new AbstractHasValue<HTMLDocument>() {

	    @Override
	    public HTMLDocument getValue() {
		return (HTMLDocument) protocolComponent.getDocument();
	    }

	    @Override
	    protected void onValueChange(HTMLDocument newValue) {
		protocolComponent.setDocument(newValue);
	    }
	};
    }

    @Override
    public HasKeyEvent getInputEvent() {
	return inputEvent;
    }

    @Override
    public HasValue<HTMLDocument> getProtocol() {
	return protocol;
    }

    @Override
    public HasValue<String> getInputValue() {
	return inputValue;
    }

}
