package org.rosuda.irconnect;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.newsclub.net.unix.AFUNIXSocketAddress;
import org.rosuda.linux.socket.NativeSocketLibUtil;
import org.rosuda.linux.socket.TcpTunnelServer;
import org.rosuda.util.process.OS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpDomainServerManager {

    public class ConnectionException extends RuntimeException {

        /**
         * 
         */
        private static final long serialVersionUID = 1613692821951838250L;

        public ConnectionException(String string) {
            super(string);
        }

    }

    private static class TcpTunnelKey {

        private String host;
        private int port;
        private String socket;

        public TcpTunnelKey(String host, int port, String socket) {
            this.host = host;
            this.port = port;
            this.socket = socket;
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder().append(host).append(port).append(socket).toHashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof TcpTunnelKey)) {
                return false;
            }
            final TcpTunnelKey key = (TcpTunnelKey) obj;
            return new EqualsBuilder().append(host, key.host).append(port, key.port).append(socket, key.socket).isEquals();
        }

    }

    private static TcpDomainServerManager instance = new TcpDomainServerManager();

    private static final Logger LOGGER = LoggerFactory.getLogger(TcpDomainServerManager.class);
    private final NativeSocketLibUtil nativeSocketLibUtil;
    private final Map<TcpTunnelKey, TcpTunnelServer> servers;

    protected TcpDomainServerManager() {
        nativeSocketLibUtil = new NativeSocketLibUtil();
        servers = new HashMap<TcpDomainServerManager.TcpTunnelKey, TcpTunnelServer>();
    }

    public static TcpDomainServerManager getInstance() {
        return instance;
    }

    public void prepareEnvironmentForTcpConnection(String host, int port, String socket) {
        if (isTcpConnectionAvailable(host, port)) {
            //TODO crate binding
            LOGGER.info("tcp connection to (" + host + "," + port + "," + socket + ") is already available.");
            return;
        } else if (socket != null && !OS.isWindows()) {
            createDomainsocketTunnelServer(host, port, socket);
            return;
        } else {
            return;
        }
    }

    private boolean isTcpConnectionAvailable(String host, int port) {
        Socket availableSocket = null;
        try {
            availableSocket = new Socket(host, port);
            boolean socketIsOpen = availableSocket.isConnected()
                    && (!availableSocket.isClosed() & !availableSocket.isInputShutdown() & !availableSocket.isOutputShutdown());
            LOGGER.info("Socket(" + host + "," + port + ") is available:" + socketIsOpen);
            return socketIsOpen;
        } catch (ConnectException c) {
            return false;
        } catch (IOException e) {
            LOGGER.warn("could not examine Socket(" + host + "," + port + ")", e);
        } finally {
            if (availableSocket != null) {
                try {
                    availableSocket.close();
                } catch (IOException e) {
                    LOGGER.warn("could not close Socket(" + host + "," + port + ")", e);
                }
            }
        }
        return false;
    }

    private void createDomainsocketTunnelServer(String host, int port, String socket) {
        final File socketFile = getSocketFile(socket);
        nativeSocketLibUtil.enableDomainSockets();
        LOGGER.info("... registering socket address : " + socketFile);
        try {
            final AFUNIXSocketAddress socketAddress = new AFUNIXSocketAddress(socketFile);
            LOGGER.info("... socketAddress registered " + socketAddress);
            final TcpTunnelKey tcpTunnelKey = new TcpTunnelKey(host, port, socket);
            final TcpTunnelServer tunnelServer = new TcpTunnelServer(host, port, socketAddress);
            this.servers.put(tcpTunnelKey, tunnelServer);
            //bind listening socketServer (new connection) on accept
            //desire to bind RConnection to SocketResource for close OP
            // return new SocketResource();
        } catch (final Exception x) {
            LOGGER.error("failed to create a Tunnel", x);
        }
    }

    private File getSocketFile(String socket) {
        LOGGER.info("check if linux domain socket \"" + socket + "\" is accessible ..");
        if (socket.startsWith("~")) {
            socket = socket.replace("~", System.getProperty("user.home"));
        }
        final File socketFile = new File(socket);
        if (!socketFile.exists()) {
            throw new ConnectionException("socket file \"" +  socketFile.getAbsolutePath() + "\" does not exists.");
        }

        if (!socketFile.canRead() || !socketFile.canWrite()) {
            throw new ConnectionException("socket file does not provide sufficent provileges: no r/w access on file \""
                    + socketFile.getAbsolutePath() + "\".");
        }
        return socketFile;
    }

    public void shutdown() {
        LOGGER.info("shutting down connectionMgr instance(tunnels=" + servers.size());
        for (final TcpTunnelServer tunnel : servers.values()) {
            tunnel.shutdown();
        }
        servers.clear();
    }
}
