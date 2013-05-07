package org.rosuda.linux.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;

import org.newsclub.net.unix.AFUNIXSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlockingTcpTunnel {

    private static final Logger LOGGER = LoggerFactory.getLogger(BlockingTcpTunnel.class);
    private final ServerSocket server;
    private final AFUNIXSocket domainsocket;
    private Collection<TcpStreamConnection> connections = new ArrayList<TcpStreamConnection>();

    public BlockingTcpTunnel(String host, int port, final AFUNIXSocket domainsocket) throws IOException {
        LOGGER.info("binding TcpTunnel ServerSocket to host='" + host + "', port='" + port + "', domainsocket = '" + domainsocket + "'");
        server = new ServerSocket();
        server.setReuseAddress(true);
        try {
            server.bind(new InetSocketAddress(host, port));
        } catch (final BindException bindException) {
            LOGGER.error("could not bind domain socket to TcpTunnel(" + host + "," + port + "," + domainsocket + ")", bindException);
            throw bindException;
        }
        this.domainsocket = domainsocket;
        LOGGER.info("TcpTunnel is connecting streams for ServerSocket to host='" + host + "', port='" + port + "' and domainsocket '"
                + domainsocket + "'.");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    LOGGER.info("waiting for a client ..");
                    final Socket socket = server.accept();
                    LOGGER.info("client accepted.");
                    //TODO join rconnection 
                    final IOStream domainToSocketStream = new IOStream(domainsocket.getInputStream(), socket.getOutputStream());
                    Thread domainToSocketThread = new Thread(domainToSocketStream);
                    domainToSocketThread.start();
                    domainToSocketThread.setName("domainToSocketThread");
                    final IOStream socketToDomainStream = new IOStream(socket.getInputStream(), domainsocket.getOutputStream());
                    connections.add(new TcpStreamConnection(socket, domainToSocketStream, socketToDomainStream));
                    Thread socketToDomainThread = new Thread(socketToDomainStream);
                    socketToDomainThread.start();
                    socketToDomainThread.setName("socketToDomainTread");
                } catch (final Exception i) {
                    throw new RuntimeException(i);
                }
            }
        }).start();
    }

    private static class TcpStreamConnection {

        private Socket socket;
        private IOStream domainToSocketStream;
        private IOStream socketToDomainStream;

        public TcpStreamConnection(Socket socket, IOStream domainToSocketStream, IOStream socketToDomainStream) {
            this.socket = socket;
            this.domainToSocketStream = domainToSocketStream;
            this.socketToDomainStream = socketToDomainStream;
        }
        
        private void close() {
            domainToSocketStream.close();
            socketToDomainStream.close();
            try {
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    private static class IOStream implements Runnable {
        private byte[] buffer = new byte[4096];
        private final InputStream source;
        private final OutputStream target;
        private boolean active = true;

        private IOStream(final InputStream in, final OutputStream out) {
            source = in;
            target = out;
        }

        @Override
        public void run() {
            while (active) {
                try {
                    while (active && source.available() > 0) {
                        int len = source.read(buffer);
                        target.write(buffer, 0, len);
                    }
                } catch (final IOException io) {

                }
            }
            LOGGER.info("TcpTunnel is no longer active, ending thread.");
        }

        void close() {
            active = false;
            disposeStreams();
        }

        private void disposeStreams() {
            try {
                source.close();
                LOGGER.info("disposed source stream");
            } catch (IOException e) {
                LOGGER.warn("could not dispose source input stream");
            }
            try {
                target.close();
                LOGGER.info("disposed target stream");
            } catch (IOException e) {
                LOGGER.warn("could not dispose target input stream");
            }
        }
    }

    public void close() {
        for (TcpStreamConnection connection : connections) {
            connection.close();
        }
        try {
            if (server.isClosed()) {
                LOGGER.info("Server is already closed.");
                return;
            }
            LOGGER.info("closing server");
            server.close();
            int shutdown_timeout = 1;
            LOGGER.info("TcpTunnel-Server is closed."+server.isClosed());
            //wait until server.isClosed ?
            //check if port is available ..
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
        try {
            domainsocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isOpen() {
        return !server.isClosed();
    }

}
