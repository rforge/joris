package org.rosuda.util.logging;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class StreamLogger implements Runnable {

	private Log log;
	private final String prefix;
	private final LogMode mode;
	private final BufferedReader reader;
	private long length;

	public StreamLogger(final Class<?> forClass, final String prefix,
			final LogMode mode, final InputStream in) {
		this.log = LogFactory.getLog(forClass);
		this.prefix = prefix;
		this.mode = mode;
		if (in == null)
			throw new NullPointerException("missing io stream for r process");
		this.reader = new BufferedReader(new InputStreamReader(in));
	}

	@Override
	public void run() {
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				length += line.length();
				switch (mode) {
				case ERROR:
					log.error(prefix + " " + line);
					break;
				case INFO:
					log.info(prefix + " " + line);
					break;
				}
			}
		} catch (final IOException e) {
			log.fatal(e);
		}
	}

	public long getLogLength() {
		return length;
	}
}