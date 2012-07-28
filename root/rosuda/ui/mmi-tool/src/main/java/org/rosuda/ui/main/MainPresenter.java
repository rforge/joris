package org.rosuda.ui.main;

import java.awt.event.KeyEvent;

import javax.swing.text.Element;
import javax.swing.text.html.HTMLDocument;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rosuda.irconnect.output.ObjectFormatter;
import org.rosuda.ui.core.mvc.HasKeyEvent;
import org.rosuda.ui.core.mvc.MVP;
import org.rosuda.ui.core.mvc.MessageBus;
import org.rosuda.ui.core.mvc.MessageBus.EventListener;
import org.rosuda.ui.handler.ModelSearchEventHandler;

public class MainPresenter implements MVP.Presenter<MainModel,MainView<?>>{

	private static final Log LOG = LogFactory.getLog(MainPresenter.class);
	
	private final ObjectFormatter objectFormatter = new ObjectFormatter();
	
	@Override
	public void bind(final MainModel model, final MainView<?> view, final MessageBus messageBus) {
		//TODO CRT listener
		view.getInput().addKeyEventListener(new HasKeyEvent.KeyListener<String>(){
			@Override
			public void onKeyEvent(HasKeyEvent.KeyEvent<String> event) {
				if (HasKeyEvent.KeyEvent.Type.KEY_UP.equals(event.getType())&&KeyEvent.VK_ENTER == event.getEvent().getKeyCode()){
					final String currentValue = event.getValue().substring(0, event.getValue().length() - 1);
					appendHTML(view, 
						new StringBuilder("<div class=\"command\">&gt; ")
								.append("<a href=\"")
								.append(currentValue)
								.append("\">")
							.append(currentValue)
								.append("</a>")
							.append("</div>").toString()
						);	
				view.getInputValue().setValue("");	
				messageBus.fireEvent(new CRTKeyEvent(currentValue));
				}
			}});
		messageBus.registerListener(new EventListener<IREXPResponseEvent>(){
			@Override
			public void onEvent(final IREXPResponseEvent event) {
				appendHTML(view, 
						new StringBuilder("<pre class=\"RCODE\">")
							.append(objectFormatter.format(event.getValue()))
						.append("</pre>").toString()		
				);
			}});
		//bind to swing context ..
		final ModelSearchEventHandler searchEventHandler = model.getContext().getBean(ModelSearchEventHandler.class);
		messageBus.registerListener(searchEventHandler);
	}

	private void appendHTML(final MainView<?> view, final String htmlText) {
		final HTMLDocument targetDoc = view.getProtocol().getValue();
		final Element body = targetDoc.getElement("htmlbody");
		final Element lastChild = body.getElement(body.getElementCount() - 1);
		try {
			targetDoc.insertAfterEnd(lastChild, htmlText);
		} catch (final Exception e) {
			LOG.error(e);
		}
	}
	public void unbind(final MainModel model, final MainView<?> view, final MessageBus messageBus) {
		// TODO Auto-generated method stub
		
	}

}
