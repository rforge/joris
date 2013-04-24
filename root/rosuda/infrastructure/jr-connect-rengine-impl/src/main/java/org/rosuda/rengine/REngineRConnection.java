package org.rosuda.rengine;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.newsclub.net.unix.AFUNIXSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;
import org.rosuda.irconnect.ARConnection;
import org.rosuda.irconnect.IRConnection;
import org.rosuda.irconnect.IREXP;
import org.rosuda.irconnect.RServerException;
import org.rosuda.linux.socket.NativeLibUtil;
import org.rosuda.linux.socket.NativeSocketLibUtil;
import org.rosuda.linux.socket.TcpTunnel;
import org.rosuda.util.process.OS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class REngineRConnection extends ARConnection implements IRConnection {

    private static Map<String, RConnection> socketConnections = new HashMap<String, RConnection>();
    private static int createdSockets = 0;

    private static NativeSocketLibUtil nativeSocketLibUtil = new NativeSocketLibUtil();
    protected static Logger logger = LoggerFactory.getLogger(REngineRConnection.class.getName());

    final RConnection delegate;
    private final String socket;
    private TcpTunnel tunnel;

    REngineRConnection(final String host, final int port, final String socket) {
        logger.info("creating new REngineRConnection(" + host + "," + port + "," + socket + ")");
        this.socket = socket;
        try {
            if (socket != null) {
                if (socketConnections.containsKey(socket)) {
                    this.delegate = socketConnections.get(socket);
                    return;
                }
                if (OS.isWindows()) {
                    throw new UnsupportedOperationException("socket connection is not available for windows");
                }
                // logger.info("BEFORE: enable domain socket socket,\n magic path = "
                // +
                // System.getProperty(NativeSocketLibUtil.ENV_NATIVE_LIBRARY_PATH)
                // + "\n path PROP_LIBRARY_LOADED = "
                // + System.getProperty(NativeSocketLibUtil.PROP_LIBRARY_LOADED)
                // );
                //
                nativeSocketLibUtil.enableDomainSockets();
                // logger.info("AFTER: creating unix domain socket,\n magic path = "
                // +
                // System.getProperty(NativeSocketLibUtil.ENV_NATIVE_LIBRARY_PATH)
                // + "\n path PROP_LIBRARY_LOADED = "
                // + System.getProperty(NativeSocketLibUtil.PROP_LIBRARY_LOADED)
                // + "\n isSupported ?"+AFUNIXSocket.isSupported());
                final File socketFile = new File(socket);
                logger.info("AFTER: socket file \"" + socketFile.getAbsolutePath() + "\" exists ? " + socketFile.exists());
                if (!socketFile.exists()) {
                    throw new RServerException(this, "socket file does not exist", "no file \"" + socketFile.getAbsolutePath()
                            + "\" exists.");
                }
                if (!socketFile.canRead() || !socketFile.canWrite()) {
                    throw new RServerException(this, "socket file does not provide sufficent provileges", "no r/w access on file \""
                            + socketFile.getAbsolutePath() + "\".");
                }
                String prop = System.getProperty("org.newsclub.net.unix.library.loaded");
                java.io.File file = prop != null ? new java.io.File(prop) : null;
                logger.info("[[BEFORE: [#created sockets:" + createdSockets + ", System classloader info = "
                        + NativeLibUtil.listLoadedLibraries() + "\n magic path = "
                        + System.getProperty(NativeSocketLibUtil.ENV_NATIVE_LIBRARY_PATH) + "\n path PROP_LIBRARY_LOADED = '" + prop
                        + "'\n is .so file present :" + (file != null ? file.length() : -1));
                // " current tc.ClassLoader ="+Thread.currentThread().getContextClassLoader());
logger.info("registering socket address : "+socketFile);
                final AFUNIXSocketAddress socketAddress = new AFUNIXSocketAddress(socketFile);
logger.info("socketAddress registered "+socketAddress);
                AFUNIXSocket domainsocket = null;
                try {
                    domainsocket = AFUNIXSocket.connectTo(socketAddress);
                } catch (final UnsatisfiedLinkError ule) {
                    final Collection<String> testStack = new ArrayList<String>();
                    for (final StackTraceElement se : Thread.currentThread().getStackTrace()) {
                        if (se.getClassName().toLowerCase().contains("test")) {
                            testStack.add(se.getClassName());
                        }
                    }
                    logger.error("illegal state !"+testStack, ule);
                }
                domainsocket = AFUNIXSocket.connectTo(socketAddress);
                createdSockets++;
                logger.info("[[AFTER: created " + createdSockets + " sockets");

                if (!domainsocket.isConnected()) {
                    throw new RServerException("no connection to unixsocket \"" + socket + "\"", "domainsocket is not connected.");
                }
                if (domainsocket.isClosed()) {
                    throw new RServerException("connection to unixsocket \"" + socket + "\" has been closed", "domainsocket is closed.");
                }
                this.tunnel = new TcpTunnel(host, port, domainsocket);
                logger.info("tunnel established, new Rconnection to " + host + "," + port);
                this.delegate = new RConnection(host, port);
                socketConnections.put(socket, this.delegate);
            } else {
                this.delegate = new RConnection(host, port);
            }
        } catch (final RserveException rse) {
            rse.printStackTrace();
            throw new RServerException(this, rse.getRequestErrorDescription(), rse.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RServerException(this, e.getLocalizedMessage(), e.getMessage());
        }
        if (this.delegate == null)
            throw new IllegalArgumentException("missing required delegate.");
    }

    public void close() {
        logger.info("closing current connection.");
        delegate.close();
        handleSocketClose();
    }

    public IREXP eval(final String query) {
        try {
            return new REngineREXP(delegate.eval(query));
        } catch (final RserveException rse) {
            if (rse.getCause() != null && rse.getCause() instanceof SocketException) {
                logger.error("SocketException on :" + query);
            }
            throw new RServerException(this, rse.getRequestErrorDescription(), rse.getMessage() + " on r-command:" + query, rse);
        }
    }

    public String getLastError() {
        return delegate.getLastError();
    }

    public boolean isConnected() {
        return delegate.isConnected();
    }

    public void shutdown() {
        logger.info("shutting down connection");
        try {
            if (delegate.isConnected()) {
                delegate.shutdown();
            }
        } catch (final RserveException rse) {
            throw new RServerException(this, rse.getRequestErrorDescription(), rse.getMessage());
        }
        handleSocketShutdown();
    }

    public void voidEval(final String query) {
        try {
            delegate.voidEval(query);
        } catch (final RserveException rse) {
            if (rse.getCause() != null && rse.getCause() instanceof SocketException) {
                logger.error("SocketException on :" + query);
            }
            throw new RServerException(this, rse.getRequestErrorDescription(), rse.getMessage() + " on r-command:" + query, rse);
        }
    }

    @Override
    protected void login(final String userName, final String userPassword) {
        try {
            delegate.login(userName, userPassword);
        } catch (final RserveException rse) {
            logger.error("Unable to connect with login", rse);
            throw new RServerException(this, rse.getRequestErrorDescription(), rse.getMessage() + " on login user \"" + userName + "\"",
                    rse);
        }
    }

    private void handleSocketShutdown() {
        handleSocketClose();
        if (this.socket == null) {
            return;
        } else {
            releaseSocketFile(socket);
        }
    }

    private void releaseSocketFile(String socketFile) {
        final File toFile = new File(socketFile);
        if (toFile.exists() && !toFile.delete()) {
            toFile.deleteOnExit();
        }

    }

    private void handleSocketClose() {
        if (this.socket == null) {
            return;
        } else {
            if (this.tunnel != null) {
                tunnel.close();
            }
            socketConnections.remove(socket);
        }
    }

}
