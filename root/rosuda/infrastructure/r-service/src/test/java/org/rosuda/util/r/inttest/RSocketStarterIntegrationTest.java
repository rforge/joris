package org.rosuda.util.r.inttest;

import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rosuda.irconnect.IRConnection;
import org.rosuda.rengine.REngineConnectionFactory;
import org.rosuda.util.process.OS;
import org.rosuda.util.process.ProcessService;
import org.rosuda.util.process.RUNSTATE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * this tests the start/stop of an REngine-Server-Process the @Ignore test cases
 * work but block maven build
 * 
 * @author ralfseger
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/r-socket-service.spring.xml" })
@Configurable
public class RSocketStarterIntegrationTest {
	

    @Autowired
    @Qualifier("rStarterProcess")
    private ProcessService<IRConnection> service;

    @Test(timeout=60000)
    public void testStartStopProcess() {
	if (OS.isWindows()) {
	    Assert.assertNotNull("windows passes for unsupported op");
	    return;
	}
	Assert.assertNotNull(service);
	service.start();
	Assert.assertEquals(RUNSTATE.RUNNING, service.getRunState());
	service.stop();
	Assert.assertEquals(RUNSTATE.TERMINATED, service.getRunState());
	try {
	    REngineConnectionFactory.getInstance().createRConnection(new Properties());
	    Assert.fail("no error raised, there is a connection available.");
	} catch (final Exception x) {
	    Assert.assertNotNull(x);
	}
    }
}
