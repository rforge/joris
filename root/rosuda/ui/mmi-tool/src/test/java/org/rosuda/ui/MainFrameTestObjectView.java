package org.rosuda.ui;

import javax.swing.text.html.HTMLDocument;

import org.rosuda.ui.core.mvc.DefaultHasKeyEvent;
import org.rosuda.ui.core.mvc.DefaultHasValue;
import org.rosuda.ui.core.mvc.DefaultTestView;
import org.rosuda.ui.core.mvc.HasKeyEvent;
import org.rosuda.ui.core.mvc.HasValue;
import org.rosuda.ui.main.MainView;

public class MainFrameTestObjectView extends DefaultTestView implements MainView<Object> {

    private HasValue<String> input = new DefaultHasValue<String>();
    private HasValue<HTMLDocument> protocol = new DefaultHasValue<HTMLDocument>();
    private HasKeyEvent inputEvent = new DefaultHasKeyEvent();

    @Override
    public HasValue<String> getInputValue() {
	return input;
    }

    @Override
    public HasValue<HTMLDocument> getProtocol() {
	return protocol;
    }

    @Override
    public HasKeyEvent getInputEvent() {
	return inputEvent;
    }

}
