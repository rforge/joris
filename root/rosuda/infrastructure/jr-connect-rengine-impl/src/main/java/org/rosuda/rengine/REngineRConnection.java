package org.rosuda.rengine;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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

public class REngineRConnection extends ARConnection implements IRConnection {

    private static Map<String, RConnection> socketConnections = new HashMap<String, RConnection>();
    private static int createdSockets = 0;

    private static NativeSocketLibUtil nativeSocketLibUtil = new NativeSocketLibUtil();
    protected static Logger logger = Logger.getLogger(REngineRConnection.class.getName());

    final RConnection delegate;
    private final String socket;
    private TcpTunnel tunnel;

    REngineRConnection(final String host, final int port, final String socket) {
        logger.info("creating new REngineRConnection("+host+","+port+","+socket+")");
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
                if (!socketFile.canRead()||!socketFile.canWrite()) {
                    throw new RServerException(this, "socket file does not provide sufficent provileges", "no r/w access on file \"" + socketFile.getAbsolutePath()
                            + "\".");                   
                }
                logger.info("[[BEFORE: [#created sockets:" + createdSockets + ", System classloader info = "
                        + NativeLibUtil.listLoadedLibraries() + "\n magic path = "
                        + System.getProperty(NativeSocketLibUtil.ENV_NATIVE_LIBRARY_PATH) + "\n path PROP_LIBRARY_LOADED = "
                        + System.getProperty(NativeSocketLibUtil.PROP_LIBRARY_LOADED) + "\n is .so file present :");
                // File(System.getProperty(NativeSocketLibUtil.PROP_LIBRARY_LOADED)).length()+
                // " current tc.ClassLoader ="+Thread.currentThread().getContextClassLoader());

                AFUNIXSocket domainsocket = AFUNIXSocket.connectTo(new AFUNIXSocketAddress(socketFile));
                createdSockets++;
                logger.info("[[AFTER: created " + createdSockets + " sockets");

                if (!domainsocket.isConnected()) {
                    throw new RServerException("no connection to unixsocket \"" + socket + "\"", "domainsocket is not connected.");
                }
                if (domainsocket.isClosed()) {
                    throw new RServerException("connection to unixsocket \"" + socket + "\" has been closed", "domainsocket is closed.");
                }
                this.tunnel = new TcpTunnel(host, port, domainsocket);
                logger.info("tunnel established, new Rconnection to "+host+","+port);
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
                logger.log(Level.SEVERE, "SocketException on :" + query);
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
                logger.log(Level.SEVERE, "SocketException on :" + query);
            }
            throw new RServerException(this, rse.getRequestErrorDescription(), rse.getMessage() + " on r-command:" + query, rse);
        }
    }

    @Override
    protected void login(final String userName, final String userPassword) {
        try {
            delegate.login(userName, userPassword);
        } catch (final RserveException rse) {
            Logger.getLogger(REngineRConnection.class.getName()).log(Level.SEVERE, null, rse);
            throw new RServerException(this, rse.getRequestErrorDescription(), rse.getMessage() + " on login user \"" + userName + "\"",
                    rse);
        }
    }

    private void handleSocketShutdown() {
        handleSocketClose();
        if (this.socket == null) {
            return;
        } else {
            nativeSocketLibUtil.releaseSocketFile(socket);
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
