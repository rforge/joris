package org.rosuda.ui.main;

import java.awt.event.KeyEvent;

import javax.swing.text.Element;
import javax.swing.text.html.HTMLDocument;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rosuda.irconnect.output.ObjectFormatter;
import org.rosuda.ui.core.mvc.HasKeyEvent;
import org.rosuda.ui.core.mvc.MVP;
import org.rosuda.ui.core.mvc.MessageBus;
import org.rosuda.ui.core.mvc.MessageBus.EventListener;

public class MainPresenter<C> implements MVP.Presenter<MainModel, MainView<C>> {

    private static final Log LOG = LogFactory.getLog(MainPresenter.class);

    private final ObjectFormatter objectFormatter = new ObjectFormatter();

    @Override
    public void bind(final MainModel model, final MainView<C> view, final MessageBus messageBus) {
	view.getProtocol().setValue(model.getProtocol());
	view.getInputEvent().addKeyEventListener(new HasKeyEvent.KeyListener() {
	    @Override
	    public void onKeyEvent(HasKeyEvent.KeyEvent event) {
		if (HasKeyEvent.KeyEvent.Type.KEY_UP.equals(event.getType()) && KeyEvent.VK_ENTER == event.getKeyCode()) {
		    final String currentValue = view.getInputValue().getValue();
		    appendHTML(model, new StringBuilder("<div class=\"command\">&gt; ").append("<a href=\"").append(StringEscapeUtils.escapeHtml(currentValue))
			    .append("\">").append(currentValue).append("</a>").append("</div>").toString());
		    view.getInputValue().setValue("");
		    messageBus.fireEvent(new CRTKeyEvent(currentValue));
		} else if (HasKeyEvent.KeyEvent.Type.KEY_UP.equals(event.getType()) && KeyEvent.VK_UP == event.getKeyCode()) {
		    
		}
		
	    }
	});
	messageBus.registerListener(new EventListener<IREXPResponseEvent>() {
	    @Override
	    public void onEvent(final IREXPResponseEvent event) {
		appendHTML(model, new StringBuilder("<pre class=\"RCODE\">").append(objectFormatter.format(event.getValue())).append("</pre>").toString());
	    }
	});
    }

    private void appendHTML(final MainModel model, final String htmlText) {
	final HTMLDocument targetDoc = model.getProtocol();
	final Element body = targetDoc.getElement("htmlbody");
	final Element lastChild = body.getElement(body.getElementCount() - 1);
	try {
	    targetDoc.insertAfterEnd(lastChild, htmlText);
	} catch (final Exception e) {
	    LOG.error(e);
	}
    }

    public void unbind(final MainModel model, final MainView<C> view, final MessageBus messageBus) {
	// TODO Auto-generated method stub

    }

}
