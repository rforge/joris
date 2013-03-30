package org.rosuda.ui.main;

import java.awt.event.KeyEvent;

import javax.swing.text.Element;
import javax.swing.text.html.HTMLDocument;

import org.apache.commons.lang.StringEscapeUtils;
import org.rosuda.irconnect.output.ObjectFormatter;
import org.rosuda.ui.core.mvc.HasKeyEvent;
import org.rosuda.ui.core.mvc.MVP;
import org.rosuda.ui.core.mvc.MessageBus;
import org.rosuda.ui.core.mvc.MessageBus.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainPresenter<C> implements MVP.Presenter<MainModel, MainView<C>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainPresenter.class);

    private final ObjectFormatter objectFormatter = new ObjectFormatter();

    @Override
    public void bind(final MainModel model, final MainView<C> view, final MessageBus messageBus) {
        view.getProtocol().setValue(model.getProtocol());
        view.getInputEvent().addKeyEventListener(new HasKeyEvent.KeyListener() {
            @Override
            public void onKeyEvent(HasKeyEvent.KeyEvent event) {
                if (HasKeyEvent.KeyEvent.Type.KEY_UP.equals(event.getType())) {
                    switch (event.getKeyCode()) {
                    case KeyEvent.VK_ENTER: {
                        final String currentValue = memorizeActuallyDisplayedCommand(model, view);
                        appendHTML(model, currentValue);
                        view.getInputValue().setValue("");
                        messageBus.fireEvent(new CRTKeyEvent(currentValue));
                    }
                        break;
                    case KeyEvent.VK_UP: {
                        memorizeActuallyDisplayedCommand(model, view);
                        final String previousCommand = model.getPreviousCommand();
                        setDisplayedCommand(view, previousCommand);
                    }
                        break;
                    case KeyEvent.VK_DOWN: {
                        memorizeActuallyDisplayedCommand(model, view);
                        final String nextCommand = model.getNextCommand();
                        setDisplayedCommand(view, nextCommand);
                    }
                        break;

                    }
                }
            }

            private String memorizeActuallyDisplayedCommand(final MainModel model, final MainView<C> view) {
                final String currentValue = view.getInputValue().getValue();
                model.addCommand(currentValue);
                return currentValue;
            }

            private void setDisplayedCommand(final MainView<C> view, final String previousCommand) {
                if (previousCommand != null) {
                    view.getInputValue().setValue(previousCommand);
                }
            }
        });
        messageBus.registerListener(new EventListener<IREXPResponseEvent>() {
            @Override
            public void onEvent(final IREXPResponseEvent event) {
                appendHTML(model,
                        new StringBuilder("<pre class=\"RCODE\">").append(objectFormatter.format(event.getValue())).append("</pre>")
                                .toString());
            }
        });
    }

    private void appendHTML(final MainModel model, final String currentValue) {
        final String htmlText = new StringBuilder("<div class=\"command\">&gt; ").append("<a href=\"")
                .append(StringEscapeUtils.escapeHtml(currentValue)).append("\">").append(currentValue).append("</a>").append("</div>")
                .toString();
        final HTMLDocument targetDoc = model.getProtocol();
        final Element body = targetDoc.getElement("htmlbody");
        final Element lastChild = body.getElement(body.getElementCount() - 1);
        try {
            targetDoc.insertAfterEnd(lastChild, htmlText);
        } catch (final Exception e) {
            LOGGER.error("could not insert child text:" + htmlText, e);
        }
    }

    public void unbind(final MainModel model, final MainView<C> view, final MessageBus messageBus) {
        // TODO Auto-generated method stub

    }

}
