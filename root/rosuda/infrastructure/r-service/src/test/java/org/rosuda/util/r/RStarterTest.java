package org.rosuda.util.r;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rosuda.irconnect.IRConnection;
import org.rosuda.rengine.REngineConnectionFactory;
import org.rosuda.util.process.ProcessService;
import org.rosuda.util.process.RUNSTATE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * this tests the start/stop of an REngine-Server-Process
 * the @Ignore test cases work but block maven build
 * @author ralfseger
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/spring/r-service.spring.xml"})
@Configurable
public class RStarterTest {
	
	
	@Autowired
	@Qualifier("rStarterProcess")
	private ProcessService<IRConnection> service;
		
	@Test
	public void testSpringServiceAvailable() {
		Assert.assertNotNull(service);
	}
		
	@Test
	public void testStartStopProcess() {
		Assert.assertNotNull(service);
		service.start();
		Assert.assertEquals(RUNSTATE.RUNNING, service.getRunState());
		service.stop();
		Assert.assertEquals(RUNSTATE.TERMINATED, service.getRunState());	
		try {
			REngineConnectionFactory.getInstance().createRConnection(null);
			Assert.fail("no error raised, there is a connection available.");
		} catch (final Exception x) {
			Assert.assertNotNull(x);
		}
	}
}
