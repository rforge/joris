package org.rosuda.linux.socket.connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class IOStream implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(IOStream.class);
    private byte[] buffer = new byte[4096];
    private final InputStream source;
    private final OutputStream target;
    private boolean active = true;
    private static boolean debug = false;
    
    IOStream(final InputStream in, final OutputStream out) {
        source = in;
        target = out;
    }

    @Override
    public void run() {
        while (active) {
            try {
                while (active && source.available() > 0) {
                    int len = source.read(buffer);
                    if (debug && len > 0) {
                        LOGGER.info("~~~transerferred (" + len + " bytes) :" + makeString(buffer, len));
                    }
                    target.write(buffer, 0, len);

                }
            } catch (final IOException io) {

            }
        }
        LOGGER.info("TcpTunnel is no longer active, ending thread.");
    }

    private String makeString(byte[] buffer, int len) {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < len; i++) {
            builder.append(buffer[i]);
        }
        return builder.toString();
    }

    void close() {
        active = false;
        disposeStreams();
    }
    
    boolean isActive() {
        return active;
    }

    private void disposeStreams() {
        try {
            source.close();
            LOGGER.debug("disposed source stream");
        } catch (IOException e) {
            LOGGER.warn("could not dispose source input stream");
        }
        try {
            target.close();
            LOGGER.debug("disposed target stream");
        } catch (IOException e) {
            LOGGER.warn("could not dispose target input stream");
        }
    }
}
