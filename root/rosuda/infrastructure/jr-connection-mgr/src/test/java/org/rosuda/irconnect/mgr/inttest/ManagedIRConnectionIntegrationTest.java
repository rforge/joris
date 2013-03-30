package org.rosuda.irconnect.mgr.inttest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rosuda.irconnect.IRConnection;
import org.rosuda.irconnect.IREXP;
import org.rosuda.util.process.ProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:spring/r-service.spring.xml", "classpath:/spring/r-manager.spring.xml" })
@Configurable
public class ManagedIRConnectionIntegrationTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManagedIRConnectionIntegrationTest.class);

    @Autowired
    IRConnection managedConnection;

    @Autowired
    ProcessService<IRConnection> rStarterProcess;

    @After
    public void onShutDown() {
        LOGGER.info("shutting down test context ...");
        managedConnection.close();
        LOGGER.info("connection closed ...");
        rStarterProcess.stop();
        LOGGER.info("stopped rStarterProcess");
    }

    @Test
    public void testIsTimpConnection() throws IOException {
        Assert.assertNotNull(managedConnection);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(
                ManagedIRConnectionIntegrationTest.class.getResourceAsStream("/TIMPExample.R")));
        String line = null;
        while ((line = reader.readLine()) != null) {
            LOGGER.info("> " + line);
            managedConnection.voidEval(line);
        }
        final IREXP success = managedConnection.eval("kinetic_model");
        Assert.assertNotNull(success);
    }
}
