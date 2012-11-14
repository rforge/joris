/*
 * This is a conveniance implementation that extracts
 * <strong>host</strong> and <strong>port</strong> from the properties file
 *
 */

package org.rosuda.irconnect;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rosuda.irconnect.proxy.RConnectionProxy;

/**
 * 
 * @author Ralf
 */
public abstract class AConnectionFactory implements IConnectionFactory {

    private static final Log log = LogFactory.getLog(AConnectionFactory.class);
    private static IConnectionFactory instance;

    private final List<IRConnection> pool = new ArrayList<IRConnection>();

    public static IConnectionFactory getInstance() {
	return instance;
    }

    protected AConnectionFactory() {
	instance = this;
    }

    public IRConnection createRConnection(final Properties configuration) {
	return createARConnection(configuration);
    }

    public ITwoWayConnection createTwoWayConnection(final Properties configuration) {
	final IRConnection connection = createARConnection(configuration);
	return RConnectionProxy.createProxy(connection, handleCreateTransfer(connection));
    }

    private final IRConnection createARConnection(final Properties configuration) {
	if (configuration == null)
	    return handleCreateConnectionProxy(default_host, default_port);
	String host = default_host;
	int port = default_port;
	if (configuration.containsKey(IConnectionFactory.HOST)) {
	    host = configuration.getProperty(IConnectionFactory.HOST);
	}
	if (configuration.containsKey(IConnectionFactory.PORT)) {
	    port = Integer.parseInt(configuration.getProperty(IConnectionFactory.PORT));
	}
	final ARConnection connection = handleCreateConnectionProxy(host, port);
	if (configuration.containsKey(IConnectionFactory.USER) && configuration.containsKey(IConnectionFactory.PASSWORD)) {
	    final String user = configuration.getProperty(IConnectionFactory.USER);
	    final String password = configuration.getProperty(IConnectionFactory.PASSWORD);
	    connection.login(user, password);
	}
	return connection;
    }

    private final ARConnection handleCreateConnectionProxy(final String host, final int port) {
	final ARConnection connection = handleCreateConnection(host, port);
	pool.add(connection);
	connection.addRConnectionListener(new IRConnectionListener() {
	    @Override
	    public void connectionPerformed(IRConnectionEvent event) {
		if (event.getType().equals(IRConnectionEvent.Type.CLOSE)) {
		    pool.remove(event.getObject());
		}
	    }
	});
	return connection;
    }

    protected abstract ARConnection handleCreateConnection(final String host, final int port);

    protected abstract IJava2RConnection handleCreateTransfer(final IRConnection con);

    @Override
    public final void shutdown() {
	if (pool.size() == 0) {
	    shutDownWithNewConnection();
	    return;
	}
	log.info("shutting down " + pool.size() + " RConnections");
	int remainingConnections = pool.size();
	try {
	    for (final IRConnection con : pool) {
		con.shutdown();
		remainingConnections --;
	    }
	} catch (final RServerException rse) {
	}
	if (remainingConnections > 0) {
	    shutDownWithNewConnection();
	}
    }

    private void shutDownWithNewConnection() {
	log.info("acquiring connection for shutdown ..");
	final IRConnection con = createARConnection(null);
	con.shutdown();
    }
}
