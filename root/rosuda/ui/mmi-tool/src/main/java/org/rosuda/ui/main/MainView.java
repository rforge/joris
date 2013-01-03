package org.rosuda.ui.main;

import javax.swing.text.html.HTMLDocument;

import org.rosuda.ui.core.mvc.HasKeyEvent;
import org.rosuda.ui.core.mvc.HasValue;
import org.rosuda.ui.core.mvc.MVP;

public interface MainView<C> extends MVP.View<C> {
    
    HasValue<String> getInputValue();

    HasValue<HTMLDocument> getProtocol();

    HasKeyEvent getInputEvent();
}
