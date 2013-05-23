package org.rosuda.integration.connection.jrengine;

import org.junit.Assert;
import org.junit.Test;
import org.rosuda.integration.suites.util.BeforePlainJavaConnectionTestSuite;
import org.rosuda.integration.suites.util.PlainJavaConnectionTestSuiteContext;
import org.rosuda.irconnect.IRConnection;
import org.rosuda.irconnect.RServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Ralf
 */
public class CreateConnectionIntegrationTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateConnectionIntegrationTest.class);

    @Deprecated
    //tested in ConnectionTestEnv
    @Test
    public void testCreateREngineConnection() {
        try {
            final IRConnection irConnection = PlainJavaConnectionTestSuiteContext.getInstance().acquireRConnection();
            Assert.assertNotNull(irConnection);
        } catch (final RServerException x) {
            LOGGER.warn("no r connection available - skipping integration test " + CreateConnectionIntegrationTest.class);
        }
    }

}
