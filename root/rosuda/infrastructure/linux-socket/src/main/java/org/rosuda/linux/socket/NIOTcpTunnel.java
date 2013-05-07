package org.rosuda.linux.socket;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import org.newsclub.net.unix.AFUNIXSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NIOTcpTunnel {

    private static final Logger LOGGER = LoggerFactory.getLogger(BlockingTcpTunnel.class);
    protected static final int MAX_BUFFER_SIZE = 4096;
    private final ServerSocketChannel serverChannel;
    private SocketChannel channel;
    private boolean open = true;
    private final AFUNIXSocket domainsocket;

    public NIOTcpTunnel(final String host, final int port, final AFUNIXSocket domainsocket) throws IOException {
        LOGGER.info("binding TcpTunnel to SocketChannel host='" + host + "', port='" + port + "', domainsocket = '" + domainsocket + "'");
        this.domainsocket = domainsocket;
        LOGGER.info("creating a SocketServer");
        final InetSocketAddress address = new InetSocketAddress(host, port);
        serverChannel = ServerSocketChannel.open();
        serverChannel.socket().bind(address);
        new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    createBinding(domainsocket, address);
                } catch (IOException e) {
                    LOGGER.error("failed to connect to ServerSocket", e);
                }
            }});
    }

    private void createBinding(final AFUNIXSocket domainsocket, final InetSocketAddress address) throws IOException {
        LOGGER.info("channel accepts serverChannel ...");
        channel = serverChannel.accept();
        channel.configureBlocking(false);
        channel.connect(address);
        LOGGER.info("waiting for channel to connect ...");
        waitUntilChannelIsConnected();
        LOGGER.info("connecting channel input to domainsocket output");
        final Thread domainSocketOutput = new Thread(new Runnable() {

            private ByteBuffer byteBuffer;

            @Override
            public void run() {
                byteBuffer = ByteBuffer.allocate(MAX_BUFFER_SIZE);
                try {
                    while (open) {
                        final int read = channel.read(byteBuffer);
                        if (read > 0) {
                            domainsocket.getOutputStream().write(byteBuffer.array(), 0, read);
                        }
                    }
                    LOGGER.info("Tunnel is closed, Thread(channelInput->domainSocketOuput) ends.");
                } catch (final IOException io) {
                    LOGGER.warn("could not write to domainsocket", io);
                }
            }
        });
        domainSocketOutput.setName("channelInput->domainSocketOuput");
        final Thread domainSocketInput = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    final InputStream domainIn = domainsocket.getInputStream();
                    byte[] buffer = new byte[MAX_BUFFER_SIZE];
                    while (open) {
                        if (domainIn.available() > 0) {
                            final int read = domainIn.read(buffer);
                            ByteBuffer byteBuffer = ByteBuffer.wrap(buffer, 0, read);
                            channel.write(byteBuffer);
                        }
                        LOGGER.info("Tunnel is closed, Thread(domainSocketInput->channelOutput) ends.");           
                    }
                } catch (final IOException io) {
                    LOGGER.warn("could not write to domainsocket", io);

                }
            }
        });
        domainSocketInput.setName("domainSocketInput->channelOutput");
        domainSocketInput.start();
        domainSocketOutput.start();
    }

    public boolean isOpen() {
        return channel.isOpen();
    }

    public void close() {
        try {
            serverChannel.close();
        } catch (IOException e) {
            LOGGER.error("error closing server channel", e);
        }
        try {
            if (channel != null) {
                channel.close();
            }
        } catch (IOException e) {
            LOGGER.error("error closing channel", e);
        }
        try {
            domainsocket.close();
        } catch (IOException e) {
            LOGGER.error("error closing domainsocket", e);
        }
    }

    private void waitUntilChannelIsConnected() throws IOException {
        while (!channel.finishConnect());
    }
}
