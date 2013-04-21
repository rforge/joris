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

public class TcpTunnel {

    private static final Logger LOGGER = LoggerFactory.getLogger(TcpTunnel.class);
    private final ServerSocket server;
    private final AFUNIXSocket domainsocket;
    private Collection<IOStream> streams = new ArrayList<IOStream>();

    public TcpTunnel(String host, int port, final AFUNIXSocket domainsocket) throws IOException {
        LOGGER.info("binding TcpTunnel ServerSocket to host='" + host + "', port='" + port + "', domainsocket = '" + domainsocket + "'");
        server = new ServerSocket();
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
                    final IOStream domainToSocketStream = new IOStream(domainsocket.getInputStream(), socket.getOutputStream());
                    streams.add(domainToSocketStream);
                    new Thread(domainToSocketStream).start();
                    final IOStream socketToDomainStream = new IOStream(socket.getInputStream(), domainsocket.getOutputStream());
                    streams.add(socketToDomainStream);
                    new Thread(socketToDomainStream).start();
                } catch (final Exception i) {
                    throw new RuntimeException(i);
                }
            }
        }).start();
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
            ;
        }

        void close() {
            active = false;
        }

    }

    public void close() {
        for (IOStream stream : streams) {
            stream.close();
        }
        try {
            server.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            domainsocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
