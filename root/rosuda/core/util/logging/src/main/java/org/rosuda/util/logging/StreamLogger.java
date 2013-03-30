package org.rosuda.util.logging;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamLogger implements Runnable {

    private Logger logger;
    private boolean running;
    private final String prefix;
    private final LogMode mode;
    private final BufferedReader reader;
    private long length;

    public StreamLogger(final Class<?> forClass, final String prefix, final LogMode mode, final InputStream in) {
	this.logger = LoggerFactory.getLogger(forClass);
	this.prefix = prefix;
	this.mode = mode;
	this.running = true;
	if (in == null)
	    throw new NullPointerException("missing io stream for r process");
	this.reader = new BufferedReader(new InputStreamReader(in));
    }

    @Override
    public void run() {
	String line = null;
	try {
	    while (running && (line = reader.readLine()) != null) {
		length += line.length();
		switch (mode) {
		case ERROR:
		    logger.error(prefix + " " + line);
		    break;
		case INFO:
		    logger.info(prefix + " " + line);
		    break;
		}
	    }
	} catch (final IOException e) {
	    logger.error("StreamLogger.run() failed.", e);
	}
    }

    public void stop() {
	running = false;
    }
    
    public long getLogLength() {
	return length;
    }
}