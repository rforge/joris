package org.rosuda.integration.suites.util;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AfterPlainJavaConnectionTestSuite {

    private static Logger LOGGER= LoggerFactory.getLogger(AfterPlainJavaConnectionTestSuite.class);
    
    @Test
    public void instanceIsRunningBeforeShutdown() {
        assertTrue(PlainJavaConnectionTestSuiteContext.getInstance().acquireRConnection().isConnected());
    }
    
    @AfterClass
    public static void tearDown() throws Exception {
        LOGGER.info("=============================================finishing===========================================================");
        PlainJavaConnectionTestSuiteContext.getInstance().shutdown();
        new AfterPlainJavaConnectionTestSuite().killAllRProcesses();
        LOGGER.info("=============================================finished============================================================");
    }

    private void killAllRProcesses() {
        try {
            Runtime.getRuntime().exec("pkill Rserve");
        } catch (IOException e) {
        }
    }
}
