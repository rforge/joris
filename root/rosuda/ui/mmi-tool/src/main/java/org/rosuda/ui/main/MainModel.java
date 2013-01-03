package org.rosuda.ui.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.text.EditorKit;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

import org.rosuda.ui.MainFrame;
import org.rosuda.ui.core.mvc.MVP;

public class MainModel implements MVP.Model {

    private final HTMLDocument protocol;
    private final List<String> commands;

    public MainModel() throws IOException {
	commands = new ArrayList<String>();
	protocol = new HTMLDocument();
	protocol.setParser(new ParserDelegator());
	BufferedReader htmlStream = null;
	try {
	    htmlStream = new BufferedReader(new InputStreamReader(MainFrame.class.getResourceAsStream("/gui/html/welcome.html")));
	    EditorKit kit = getEditorKit();
	    kit.read(htmlStream, protocol, 0);
	} catch (Exception e) {
	    throw new RuntimeException(e);
	} finally {
	    if (htmlStream != null) {
		htmlStream.close();
	    }
	}
    }

    private EditorKit getEditorKit() {
	return new HTMLEditorKit();
    }

    HTMLDocument getProtocol() {
	return protocol;
    }
    
    public List<String> getCommands() {
	return Collections.unmodifiableList(commands);
    }
}
