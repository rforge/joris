package org.rosuda.linux.socket;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;

import org.newsclub.net.unix.AFUNIXSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;
import org.rosuda.linux.socket.connection.TcpDomainTunnelConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpTunnelServer {

    private static int threadcounter = 0;
    private static final Logger LOGGER = LoggerFactory.getLogger(BlockingTcpTunnel.class);
    private boolean running = true;
    private final ServerSocket server;
    private Collection<TcpDomainTunnelConnection> connections = new ArrayList<TcpDomainTunnelConnection>();
    
    public TcpTunnelServer(String host, int port, final AFUNIXSocketAddress domainsocketAddress) throws IOException {
        LOGGER.info("binding TcpTunnel ServerSocket to host='" + host + "', port='" + port + "', domainsocket = '" + domainsocketAddress + "'");
        final int currentThreadCount = ++threadcounter;
        server = new ServerSocket();
        server.setReuseAddress(true);
        try {
            server.bind(new InetSocketAddress(host, port));
        } catch (final BindException bindException) {
            LOGGER.error("could not bind domain socket to TcpTunnel(" + host + "," + port + "," + domainsocketAddress + ")", bindException);
            throw bindException;
        }
        LOGGER.info("TcpTunnel is connecting streams for ServerSocket to host='" + host + "', port='" + port + "' and domainsocket '"
                + domainsocketAddress + "'.");
        final Thread serverThread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (running) {
                    LOGGER.info("waiting for a client ..");
                    try {
                        final Socket socket = server.accept();
                        // want to link rconnection!
                        if (!running) {
                            LOGGER.warn("client was accepted, but server is already closed. exiting.");
                            return;
                        }
                        LOGGER.info("client accepted, port:"+socket.getPort()+", channel:"+socket.getChannel());    
                        socket.setReuseAddress(true);
                        LOGGER.info("creating LDS");
                        final AFUNIXSocket domainsocket = AFUNIXSocket.connectTo(domainsocketAddress);
                        TcpDomainTunnelConnection tcpDomainTunnelConnection = new TcpDomainTunnelConnection(TcpTunnelServer.this, domainsocket, socket);
                        connections.add(tcpDomainTunnelConnection);                        
                    } catch (final Exception e) {
                        LOGGER.warn("failure during socket loop", e);
                        // throw new RuntimeException(i);
                    }
                }
            }
        });
        serverThread.setName("TcpServerThread[" + currentThreadCount + "]");
        serverThread.start();
    }


    public void closeConnections() {
        try {
            LOGGER.info("closing tunneled connection streams");
            for (TcpDomainTunnelConnection connection : connections) {
                if (connection.isOpen()) {
                    connection.close();
                }
            }
            connections.clear();
        } catch (final Exception x) {
            LOGGER.error("failed to close open tunnel connections", x);
        }
    }

    // -> shutdown
    public void shutdown() {
        LOGGER.info("shutting down TcpTunnelServer.");
        closeConnections();
        try {
            if (server.isClosed()) {
                LOGGER.info("Server is already closed.");
                return;
            }
            LOGGER.info("shutting down TcpTunnelServer server");
            running = false;
            server.close();
            int shutdown_timeout = 1;
            LOGGER.info("TcpTunnelServer is closed." + server.isClosed());
            // wait until server.isClosed ?
            // check if port is available ..
            synchronized (server) {
                try {
                    server.wait(shutdown_timeout);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            LOGGER.warn("while shutting down TcpTunnel", e);
        }
    }

    public boolean isOpen() {
        return !server.isClosed();
    }

}
