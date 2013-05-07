/*
 * This is a conveniance implementation that extracts
 * <strong>host</strong> and <strong>port</strong> from the properties file
 *
 */

package org.rosuda.irconnect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.rosuda.irconnect.proxy.RConnectionProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Ralf
 */
public abstract class AConnectionFactory implements IConnectionFactory {

    private static final int TIMEOUT = 7;
    private static final Logger LOGGER = LoggerFactory.getLogger(AConnectionFactory.class);
    private static IConnectionFactory instance;
    private TcpDomainServerManager connectionMgr;

    private final List<IRConnection> pool = new ArrayList<IRConnection>();

    public static IConnectionFactory getInstance() {
        return instance;
    }

    protected AConnectionFactory() {
        instance = this;
        connectionMgr = TcpDomainServerManager.getInstance();
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
        if (configuration == null) {
            return handleCreateConnectionProxy(default_host, default_port, null);
        }
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
        connectionMgr.prepareEnvironmentForTcpConnection(host, port, socket);
        final ARConnection connection = acquireRConnection(host, port, socket);
        if (connection == null) {
            throw new RServerException((IRConnection) null, "connectionFactory is unable to provide more connections");
        } 
        LOGGER.info("adding connection#" + connection.hashCode() + " to pool.");
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
    
    //add fallback method if socket is used ..

    private ARConnection acquireRConnection(final String host, final int port, final String socket) {
        //use timeout here, either event AFTERCONNECT is published, or we are locked
        // in case of lock the magic bytes could be sent .. // ? blockingCallback ?
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<ARConnection> futureConnection = executor.submit(new Callable<ARConnection>() {

            @Override
            public ARConnection call() throws Exception {
                return handleCreateConnection(host, port);
            }
        });
        ARConnection connection = null;
        try {
            connection = futureConnection.get(TIMEOUT, TimeUnit.SECONDS);
        } catch (final Exception e) {
            LOGGER.error("could not create RConnection until timeout ..");
            if (socket != null) {
                //add fallback method if socket is used ..
                LOGGER.info("restarting Socket Server!");
            }
        }
        return connection;

    }

    protected abstract ARConnection handleCreateConnection(final String host, final int port);

    protected abstract IJava2RConnection handleCreateTransfer(final IRConnection con);

    @Override
    public final void shutdown(final Properties properties) {
        try {
            if (pool.size() == 0) {
                shutDownWithNewConnection(properties);
                return;
            }
            LOGGER.info("shutting down " + pool.size() + " RConnections");
            int shutdownSuccessCount = 0;
            final Collection<IRConnection> deadConnections = new ArrayList<IRConnection>();
            for (final IRConnection con : pool) {
                try {
                    con.shutdown();
                    deadConnections.add(con);
                    shutdownSuccessCount++;
                } catch (final RServerException rse) {
                    LOGGER.warn("r-connection shutdown failed", rse);
                }
            }
            pool.removeAll(deadConnections);
            if (shutdownSuccessCount == 0) {
                shutDownWithNewConnection(properties);
            }
        } finally {
            connectionMgr.shutdown(); 
        }
    }

    private void shutDownWithNewConnection(final Properties properties) {
        LOGGER.info("acquiring connection for shutdown ..");
        final IRConnection con = createARConnection(properties);
        con.shutdown();
    }
    
}
