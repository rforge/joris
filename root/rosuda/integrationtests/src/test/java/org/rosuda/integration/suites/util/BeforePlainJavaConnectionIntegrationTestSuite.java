package org.rosuda.integration.suites.util;

import static org.junit.Assert.assertNotNull;

import org.junit.BeforeClass;
import org.junit.Test;
import org.rosuda.irconnect.ITwoWayConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeforePlainJavaConnectionIntegrationTestSuite{
   
    private static final Logger LOGGER = LoggerFactory.getLogger(BeforePlainJavaConnectionIntegrationTestSuite.class);
    
    @BeforeClass
    public static void setupAll() {
        LOGGER.info("creating connection test environment");
        PlainJavaConnectionTestSuiteContext.getInstance();
    }
    
    
       
    @Test
    public void initCreatesAConnection() {
        ITwoWayConnection connection = PlainJavaConnectionTestSuiteContext.getInstance().acquireAssignableRConnection();
        assertNotNull(connection);
    }
    
   

}
