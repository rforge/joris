package org.rosuda.util.r.inttest;

import org.junit.Assert;
import org.junit.Test;
import org.rosuda.irconnect.IRConnection;
import org.rosuda.rengine.REngineConnectionFactory;
import org.rosuda.util.process.OS;
import org.rosuda.util.process.ProcessService;
import org.rosuda.util.process.RUNSTATE;
import org.rosuda.util.r.impl.RStartContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public abstract class AbstractStarterIntegrationTest {

    @Autowired
    @Qualifier("rStarterProcess")
    private ProcessService<IRConnection> service;
    
    @Autowired
    private RStartContext startContext;

    public AbstractStarterIntegrationTest() {
        super();
    }

    @Test(timeout = 60000)
    public void testStartStopProcess() {
        if (OS.isWindows()) {
            Assert.assertNotNull("windows passes for unsupported op");
            return;
        }
        Assert.assertNotNull(service);
        service.start();
        canCreateConnection();
        Assert.assertEquals(RUNSTATE.RUNNING, service.getRunState());
        service.stop();
        Assert.assertEquals(RUNSTATE.TERMINATED, service.getRunState());
        try {
            canCreateConnection();
            Assert.fail("no error raised, there is a connection available.");
        } catch (final Exception x) {
            Assert.assertNotNull(x);
        }
    }

    private void canCreateConnection() {
        REngineConnectionFactory.getInstance().createRConnection(startContext.getMergedConnectionProperties());
    }

}