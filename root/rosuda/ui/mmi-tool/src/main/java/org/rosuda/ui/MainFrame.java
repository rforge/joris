package org.rosuda.ui;

import java.awt.Container;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JTextArea;

import org.rosuda.irconnect.IRConnection;
import org.rosuda.irconnect.IREXP;
import org.rosuda.irconnect.IREXPConstants;
import org.rosuda.ui.context.UIContext;
import org.rosuda.ui.core.mvc.MessageBus;
import org.rosuda.ui.core.mvc.MessageBus.EventListener;
import org.rosuda.ui.event.QuitEvent;
import org.rosuda.ui.main.CRTKeyEvent;
import org.rosuda.ui.main.IREXPResponseEvent;
import org.rosuda.ui.main.MainModel;
import org.rosuda.ui.main.MainPresenter;
import org.rosuda.ui.main.MainView;
import org.rosuda.ui.main.MainViewContainerImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.swixml.SwingEngine;

public class MainFrame extends JFrame implements UIContext {

    /**
	 * 
	 */
    private static final long serialVersionUID = -6797582948731429567L;

    private final ApplicationContext context;
    private IRConnection rConnection;
    private JEditorPane protocol;
    private JTextArea input;

    public JMenuItem quit;
    public JMenuItem scanWorkspace;

    public JMenuItem searchData;

    public class Impl<C extends Container> {

	private final MainPresenter<C> presenter;
	private final MainModel model;
	private final MainView<C> view;

	public Impl(final C panel, ApplicationContext context) throws Exception {
	    this.presenter = new MainPresenter<C>();
	    this.model = new MainModel();
	    // TODO: push panel, input and SwingEngine .. to viewImpl!
	    this.view = new MainViewContainerImpl<C>(panel, input, protocol);
	    presenter.bind(model, view, context.getBean(MessageBus.class));
	}

    }

    //
    // actions: load dataset = call R source
    // * create model = call R source
    // operations(store(name, workspace)
    //

    public MainFrame() throws Exception {
	context = new ClassPathXmlApplicationContext(new String[] { "classpath*:spring/hibernate-service-impl.spring.xml",
		"classpath:/spring/mmi-jrConnectionContext.spring.xml", "classpath*:spring/r-service.spring.xml",
		"classpath:/spring/mmi-databaseContext.spring.xml", "classpath:/spring/mmi-applicationContext.spring.xml" });
	rConnection = context.getBean("managedConnection", IRConnection.class);

	// TODO load this file Localized
	final InputStream rsc = MainFrame.class.getResourceAsStream("/gui/main.xml");
	final BufferedReader reader = new BufferedReader(new InputStreamReader(rsc));
	final JFrame frame = new SwingEngine<JFrame>(this).render(reader);
	reader.close();
	final MessageBus bus = context.getBean(MessageBus.class);
	frame.addWindowListener(new WindowAdapter() {
	    @Override
	    public void windowClosing(WindowEvent e) {
		bus.fireEvent(new QuitEvent());
		super.windowClosing(e);
	    }
	});
	frame.setVisible(true);
	input.requestFocus();
	new Impl<JComponent>(this.getRootPane(), context);
	bus.registerListener(new EventListener<CRTKeyEvent>() {
	    @Override
	    public void onEvent(final CRTKeyEvent crtEvent) {
		final IREXP rexp = rConnection.eval(crtEvent.getValue());
		if (rexp == null || rexp.getType() == IREXPConstants.XT_NULL) {
		    return;
		}
		bus.fireEvent(new IREXPResponseEvent(rexp));
	    }
	});
	new UIProcessor().bindEvents(bus, this, this);
    }

    public static void main(String[] args) throws Exception {
	new MainFrame();
    }

    @Override
    public Window getUIFrame() {
	return this;
    }

    @Override
    public ApplicationContext getAppContext() {
	return context;
    }
}
