package org.rosuda.integration.suites.util;

import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.Test;
import org.rosuda.util.java.RServeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AfterPlainJavaConnectionTestSuite {

    private static Logger LOGGER = LoggerFactory.getLogger(AfterPlainJavaConnectionTestSuite.class);

    @Test
    public void instanceIsRunningBeforeShutdown() {
        assertTrue(PlainJavaConnectionTestSuiteContext.getInstance().acquireRConnection().isConnected());
    }

    @AfterClass
    public static void tearDown() throws Exception {
        LOGGER.info("=============================================finishing===========================================================");
        PlainJavaConnectionTestSuiteContext.getInstance().shutdown();
        RServeUtil.killAllUXRProcesses();
        LOGGER.info("=============================================finished============================================================");
    }
}
