package org.rosuda.ui.main;

import java.awt.Container;

import javax.swing.JEditorPane;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.html.HTMLDocument;

import org.rosuda.mvc.swing.DocumentHasValue;
import org.rosuda.mvc.swing.DocumentValueAdapter;
import org.rosuda.mvc.swing.JTextComponentHasKeyEvent;
import org.rosuda.mvc.swing.MVPContainerView;
import org.rosuda.ui.core.mvc.HasKeyEvent;
import org.rosuda.ui.core.mvc.HasValue;

public class MainViewContainerImpl extends MVPContainerView<Container> implements MainView<Container> {

	private final HasValue<String> inputValue;
	private final HasKeyEvent<String> input;
	private final HasValue<HTMLDocument> protocol;
	
	public MainViewContainerImpl(final Container container, final JTextComponent textComponent, final JEditorPane protocolComponent) throws Exception {
		super(container);
		this.input = new JTextComponentHasKeyEvent<String>(textComponent, new DocumentValueAdapter.String(textComponent.getDocument()));
		this.inputValue = new DocumentHasValue<String>(textComponent.getDocument(), new DocumentValueAdapter.String(textComponent.getDocument()));
		final Document protocolDocument = protocolComponent.getDocument();
		final HTMLDocument htmlDocument;
		if (!(protocolDocument instanceof HTMLDocument)) {
			htmlDocument = new HTMLDocument();
			protocolComponent.setContentType("text/html");
			protocolComponent.setDocument(htmlDocument);
			final String oldContent = protocolDocument.getText(0, protocolDocument.getLength());
			protocolComponent.setText(oldContent);
		} else {
			htmlDocument = (javax.swing.text.html.HTMLDocument)protocolComponent.getDocument();
		}
		this.protocol = new DocumentHasValue<HTMLDocument>(htmlDocument, new DocumentValueAdapter.HTMLDocument(htmlDocument));
		//this.protocol = new DocumentHasValue<Document>(protocolComponent.getDocument(), new DocumentValueAdapter.Document(protocolComponent));

//		final InputStream rsc = MainViewContainerImpl.class.getResourceAsStream("/gui/MainViewContainerImpl.xml");
//		final BufferedReader reader = new BufferedReader(new InputStreamReader(
//				rsc));
//		new SwingEngine<Container>(container).render(reader);
//		reader.close();
	}

	@Override
	public HasKeyEvent<String> getInput() {
		return input;
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
