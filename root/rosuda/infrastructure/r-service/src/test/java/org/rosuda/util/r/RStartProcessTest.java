package org.rosuda.util.r;

import java.io.IOException;
import java.io.InputStream;

import mockit.Mock;
import mockit.Mockit;
import mockit.NonStrict;
import mockit.NonStrictExpectations;
import mockit.Verifications;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.rosuda.irconnect.IRConnection;
import org.rosuda.irconnect.RServerException;
import org.rosuda.rengine.REngineConnectionFactory;
import org.rosuda.util.process.ProcessService;
import org.rosuda.util.process.RUNSTATE;
import org.rosuda.util.r.impl.RStartContext;
import org.rosuda.util.r.impl.RStarterFactory;

/**
 * this tests the start/stop of an REngine-Server-Process the @Ignore test cases
 * work but block maven build
 * 
 * @author ralfseger
 * 
 */
public class RStartProcessTest {

	private ProcessService<IRConnection> service;

	// use JMockit
	@NonStrict
	RStartContext setup;
	@NonStrict
	static IRConnection connection;
	@NonStrict
	static Process process;
	@NonStrict
	InputStream anyInputStream;
	
	public static class MockSetupNotRunning {
		int count = 0;
		
		@Mock
		public IRConnection createConnection() {
			if (count == 0) {
				count ++;
				throw new RServerException("not available", "error");
			}
			return connection;
		}

		@Mock
		public Process createProcessForArgs(final String[] runtimeArgs) throws IOException {
			return process;
		}
	}		

	@Ignore
	@Before
	public void setUp() throws IOException {
		final RStarterFactory factory = new RStarterFactory();
		this.service = factory.createService();
		new NonStrictExpectations() {
			{
				setup.createProcessForArgs((String[])any); returns (process);
//				process.getInputStream(); returns (anyInputStream);
//				process.getErrorStream(); returns (anyInputStream);	
			}
		};
	}
	 
	@Ignore
	@Test
	public void testStartNotRunning() throws Exception {
		//starting: no rserve is started so we get an exception
		Mockit.setUpMock(RStartContext.class, MockSetupNotRunning.class);	
		try {
			setup.createConnection();
			Assert.fail("exception not thrown");
		} catch (final RServerException rse) {
		}
		Assert.assertNotNull(service);
		//when the service."start" is invoked an connection has to be created (in case everything is installed all right)
		service.start();
		new Verifications() {
			{
				setup.createProcessForArgs((String[])any); times = 1;
			}
		};
		Assert.assertEquals(RUNSTATE.RUNNING, service.getRunState());
	}

 @Ignore
	@Test
	public void testStopProcess() {
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
		new Verifications() {
			{
				process.destroy(); times = 1;
			}
		};
	}
}
