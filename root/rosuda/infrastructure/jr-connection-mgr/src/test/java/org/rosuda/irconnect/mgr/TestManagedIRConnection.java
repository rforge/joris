package org.rosuda.irconnect.mgr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rosuda.irconnect.IRConnection;
import org.rosuda.irconnect.IREXP;
import org.rosuda.util.process.ProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:spring/r-service.spring.xml", "classpath:/spring/r-manager.spring.xml"})
@Configurable
public class TestManagedIRConnection {

	Log LOG = LogFactory.getLog(TestManagedIRConnection.class);
	
	@Autowired IRConnection managedConnection;
	
	@Autowired ProcessService<IRConnection> rStarterProcess;
	
	@After
	public void onShutDown() {
		LOG.info("shutting down test context ...");
		managedConnection.close();
		LOG.info("connection closed ...");
		rStarterProcess.stop();
		LOG.info("stopped rStarterProcess");
	}
		
	@Test
	public void testIsTimpConnection() throws IOException {
		Assert.assertNotNull(managedConnection);
		final BufferedReader reader = new BufferedReader(new InputStreamReader(TestManagedIRConnection.class.getResourceAsStream("/TIMPExample.R")));
		String line = null;
		while ((line = reader.readLine()) != null) {
			LOG.info("> "+line);
			managedConnection.voidEval(line);
		}
		final IREXP success = managedConnection.eval("kinetic_model");
		Assert.assertNotNull(success);
	}
}
