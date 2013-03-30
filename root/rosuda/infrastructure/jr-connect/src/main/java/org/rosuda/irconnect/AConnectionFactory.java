/*
 * This is a conveniance implementation that extracts
 * <strong>host</strong> and <strong>port</strong> from the properties file
 *
 */

package org.rosuda.irconnect;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.rosuda.irconnect.proxy.RConnectionProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Ralf
 */
public abstract class AConnectionFactory implements IConnectionFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(AConnectionFactory.class);
    private static IConnectionFactory instance;

    private final List<IRConnection> pool = new ArrayList<IRConnection>();

    public static IConnectionFactory getInstance() {
        return instance;
    }

    protected AConnectionFactory() {
        instance = this;
    }

    // add shell context
    public IRConnection createRConnection(final Properties configuration) {
        return createARConnection(configuration);
    }

    public ITwoWayConnection createTwoWayConnection(final Properties configuration) {
        final IRConnection connection = createARConnection(configuration);
        return RConnectionProxy.createProxy(connection, handleCreateTransfer(connection));
    }

    private final IRConnection createARConnection(final Properties configuration) {
        if (configuration == null)
            return handleCreateConnectionProxy(default_host, default_port, null);
        String host = default_host;
        int port = default_port;
        String socket = null;
        if (configuration.containsKey(IConnectionFactory.HOST)) {
            host = configuration.getProperty(IConnectionFactory.HOST);
        }
        if (configuration.containsKey(IConnectionFactory.PORT)) {
            port = Integer.parseInt(configuration.getProperty(IConnectionFactory.PORT));
        }
        if (configuration.containsKey(IConnectionFactory.SOCKET)) {
            socket = configuration.getProperty(IConnectionFactory.SOCKET);
        }
        final ARConnection connection = handleCreateConnectionProxy(host, port, socket);
        if (configuration.containsKey(IConnectionFactory.USER) && configuration.containsKey(IConnectionFactory.PASSWORD)) {
            final String user = configuration.getProperty(IConnectionFactory.USER);
            final String password = configuration.getProperty(IConnectionFactory.PASSWORD);
            connection.login(user, password);
        }
        return connection;
    }

    private final ARConnection handleCreateConnectionProxy(final String host, final int port, final String socket) {
        final ARConnection connection = handleCreateConnection(host, port, socket);
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

    protected abstract ARConnection handleCreateConnection(final String host, final int port, final String socket);

    protected abstract IJava2RConnection handleCreateTransfer(final IRConnection con);

    @Override
    public final void shutdown(final Properties properties) {
        if (pool.size() == 0) {
            shutDownWithNewConnection(properties);
            return;
        }
        LOGGER.info("shutting down " + pool.size() + " RConnections");
        int shutdownSuccessCount = 0;
        for (final IRConnection con : pool) {
            try {
                con.shutdown();
                shutdownSuccessCount++;
            } catch (final RServerException rse) {
                LOGGER.warn("r-connection shutdown failed", rse);
            }
        }
        if (shutdownSuccessCount == 0) {
            shutDownWithNewConnection(properties);
        }
    }

    private void shutDownWithNewConnection(final Properties properties) {
        LOGGER.info("acquiring connection for shutdown ..");
        final IRConnection con = createARConnection(properties);
        con.shutdown();
    }
}
