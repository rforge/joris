package org.rosuda.util.r.inttest;

import java.util.Properties;

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
	
/*	
	@BeforeClass
	public static void initClass() {
		final URL libnative = RSocketStarterIntegrationTest.class.getResource("/lib-native");
		File file;
		try {
			file = new File(libnative.toURI());
		} catch (URISyntaxException usi) {
			file = new File(libnative.getPath());
		} catch (IllegalArgumentException iae) {
			try {
				LOG.error("could not process URI:"+libnative.toURI());
			} catch (URISyntaxException e) {
				LOG.error("could not process URL:"+libnative);	
			}
			file = new File(libnative.getPath());
		}
		LOG.info("resource \""+file.getAbsolutePath()+"\" used for 'org.newsclub.net.unix.library.path'");
		System.setProperty("org.newsclub.net.unix.library.path", file.getAbsolutePath());
	}
*/
    @Autowired
    @Qualifier("rStarterProcess")
    private ProcessService<IRConnection> service;

    @Test//(timeout=30000)
    public void testStartStopProcess() {
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
