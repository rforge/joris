package org.rosuda.rengine;

import org.rosuda.irconnect.AConnectionFactory;
import org.rosuda.irconnect.ARConnection;
import org.rosuda.irconnect.IConnectionFactory;
import org.rosuda.irconnect.IJava2RConnection;
import org.rosuda.irconnect.IRConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class REngineConnectionFactory extends AConnectionFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(REngineConnectionFactory.class);
    private static final AConnectionFactory instance = new REngineConnectionFactory();
    
    public static IConnectionFactory getInstance() {
        return instance;
    }

    @Override
    protected ARConnection handleCreateConnection(final String host, final int port) {
        LOGGER.info("creating an REngineRConnection("+host+","+port+")");
        return new REngineRConnection(host, port);
    }

    @Override
    protected IJava2RConnection handleCreateTransfer(final IRConnection con) {
        if (!(con instanceof REngineRConnection))
            throw new IllegalArgumentException("Unsupported type:" + con);
        return new REngineJava2RConnection((REngineRConnection) con);
    }

}
