 package org.rosuda.ui;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JTextArea;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rosuda.irconnect.IRConnection;
import org.rosuda.irconnect.IREXP;
import org.rosuda.irconnect.mgr.IRConnectionMgrImpl;
import org.rosuda.ui.core.mvc.MessageBus;
import org.rosuda.ui.core.mvc.MessageBus.EventListener;
import org.rosuda.ui.main.CRTKeyEvent;
import org.rosuda.ui.main.IREXPResponseEvent;
import org.rosuda.ui.main.MainModel;
import org.rosuda.ui.main.MainPresenter;
import org.rosuda.ui.main.MainView;
import org.rosuda.ui.main.MainViewContainerImpl;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.swixml.SwingEngine;

public class MainFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6797582948731429567L;

	private static final Log LOG = LogFactory.getLog(MainFrame.class);

	private IRConnection rConnection;
	private JEditorPane protocol;
	private JTextArea input;

	private JMenuItem quit;
	
	public class JPanelImpl {

		private final MainPresenter presenter;
		private final MainModel model;
		private final MainView<Container> view;

		public JPanelImpl(final Container panel, final MessageBus mb) throws Exception {
			this.presenter = new MainPresenter();
			this.model = new MainModel();
			this.view = new MainViewContainerImpl(panel, input, protocol);
			presenter.bind(model, view, mb);
		}

	}

	protected void startContext() {
		final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				new String[]{
					"classpath*:spring/r-service.spring.xml",
					"classpath:/spring/applicationContext.spring.xml"});
		rConnection = context.getBean("managedConnection", IRConnection.class);
	}

	//
	// actions: load dataset = call R source
	// * create model = call R source
	// operations(store(name, workspace)
	//

	public MainFrame() throws Exception {
		startContext();
		//TODO load this file Localized
		final InputStream rsc = MainFrame.class.getResourceAsStream("/gui/Main.xml");
		final BufferedReader reader = new BufferedReader(new InputStreamReader(
				rsc));
		final JFrame frame = new SwingEngine<JFrame>(this).render(reader);
		reader.close();
		final MessageBus bus = MessageBus.INSTANCE;
		// l8n ?
		// read text
		final BufferedReader htmlStream = new BufferedReader(
				new InputStreamReader(
						MainFrame.class
								.getResourceAsStream("/gui/html/welcome.html")));
		final StringBuilder htmlBuilder = new StringBuilder();
		String line = null;
		while ((line = htmlStream.readLine()) != null) {
			htmlBuilder.append(line);
			htmlBuilder.append("\r\n");
		}
		htmlStream.close();
		frame.setVisible(true);
		protocol.setText(htmlBuilder.toString());
		input.requestFocus();
		new JPanelImpl(this.getRootPane(), bus);
		bus.registerListener(new EventListener<CRTKeyEvent>() {
			@Override
			public void onEvent(final CRTKeyEvent crtEvent) {
				final IREXP rexp = rConnection.eval(crtEvent.getValue());
				bus.fireEvent(new IREXPResponseEvent(rexp));				
			}
		});
		quit.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				System.exit(0);
			}
		});
	}

	public static void main(String[] args) throws Exception {
		new MainFrame();
	}
}
