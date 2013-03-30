package org.rosuda.irconnect.inttest;

import org.junit.Assert;
import org.junit.Test;
import org.rosuda.irconnect.IRConnection;
import org.rosuda.irconnect.RServerException;
import org.rosuda.rengine.REngineConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Ralf
 */
public class CreateConnectionIntegrationTest extends AbstractRIntegrationTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateConnectionIntegrationTest.class);

    @Test
    public void testCreateREngineConnection() {
        try {
            final IRConnection irConnection = new REngineConnectionFactory().createRConnection(null);
            Assert.assertNotNull(irConnection);
            irConnection.close();
        } catch (final RServerException x) {
            LOGGER.warn("no r connection available - skipping integration test " + CreateConnectionIntegrationTest.class);
        }
    }

}
