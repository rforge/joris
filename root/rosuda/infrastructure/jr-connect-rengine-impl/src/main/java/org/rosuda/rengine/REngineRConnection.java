package org.rosuda.rengine;

import java.net.SocketException;

import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;
import org.rosuda.irconnect.ARConnection;
import org.rosuda.irconnect.IRConnection;
import org.rosuda.irconnect.IRConnectionEvent;
import org.rosuda.irconnect.IREXP;
import org.rosuda.irconnect.RServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class REngineRConnection extends ARConnection implements IRConnection {

    protected static Logger logger = LoggerFactory.getLogger(REngineRConnection.class.getName());

    final RConnection delegate;

    REngineRConnection(final String host, final int port) {
        notifyListeners(new IRConnectionEvent.Event(IRConnectionEvent.Type.BEFORECONNECT, null, null));
        logger.info("creating new REngineRConnection(" + host + "," + port + ")");
        try {
            this.delegate = new RConnection(host, port);
            notifyListeners(new IRConnectionEvent.Event(IRConnectionEvent.Type.AFTERCONNECT, null, delegate));            
        } catch (final RserveException rse) {
            rse.printStackTrace();
            throw new RServerException(this, rse.getRequestErrorDescription(), rse.getMessage());
        }        notifyListeners(new IRConnectionEvent.Event(IRConnectionEvent.Type.CLOSE, null, delegate));

        if (this.delegate == null)
            throw new IllegalArgumentException("missing required delegate.");
    }

    public void close() {
        logger.info("closing current connection.");
        delegate.close();
        notifyListeners(new CloseInternalEvent(this));
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

    protected static class CloseInternalEvent implements IRConnectionEvent {

        private final ARConnection connection;
        
        CloseInternalEvent(final ARConnection connection) {
            this.connection = connection;
        }
        @Override
        public Type getType() {
            return Type.AFTERCONNECTIONCLOSED;
        }

        @Override
        public String getMessage() {
            return "close-rengine-r-connection";
        }

        @Override
        public Object getObject() {
            return connection;
        }
        
        protected ARConnection getConnection() {
            return connection;
        } 
    }
}
