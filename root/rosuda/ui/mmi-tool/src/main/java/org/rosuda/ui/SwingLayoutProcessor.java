package org.rosuda.ui;

import java.awt.Container;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.JXTreeTable;
import org.swixml.SwingEngine;
import org.swixml.factory.BeanFactory;

public class SwingLayoutProcessor {

    public static <T extends Container> void processLayout(T container, String resourceName) throws Exception {
	final InputStream rsc = MainFrame.class.getResourceAsStream(resourceName);
	final BufferedReader reader = new BufferedReader(new InputStreamReader(rsc));
	final SwingEngine<T> renderer = new SwingEngine<T>(container);
	renderer.getTaglib().registerTag("treetable", new BeanFactory(JXTreeTable.class));
	renderer.getTaglib().registerTag("jxtree", new BeanFactory(JXTree.class));
	renderer.getTaglib().registerTag("jxtable", new BeanFactory(JXTable.class));
	renderer.render(reader);
	reader.close();
    }
}
