package org.rosuda.linux.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.newsclub.net.unix.AFUNIXSocket;

public class TcpTunnel {

	private static final Log LOG = LogFactory.getLog(TcpTunnel.class);
	private final ServerSocket server;
	private final AFUNIXSocket domainsocket;
	private Collection<IOStream> streams = new ArrayList<IOStream>();
	
	public TcpTunnel(String host, int port, final AFUNIXSocket domainsocket)
			throws IOException {
		server = new ServerSocket();
		server.bind(new InetSocketAddress(host, port));
		this.domainsocket =domainsocket;
		LOG.info("binding TcpTunner ServerSocket to host='" + host
				+ "', port='" + port + "'.");
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					LOG.info("waiting for a client ..");
					final Socket socket = server.accept();
					LOG.info("client accepted.");
					final IOStream domainToSocketStream = new IOStream(domainsocket.getInputStream(),
							socket.getOutputStream());
					streams.add(domainToSocketStream);
					new Thread(domainToSocketStream).start();
					final IOStream socketToDomainStream = new IOStream(socket.getInputStream(),
							domainsocket.getOutputStream());
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
			};
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
