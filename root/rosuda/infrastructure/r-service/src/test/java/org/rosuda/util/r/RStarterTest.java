package org.rosuda.util.r;

import junit.framework.TestCase;

import org.rosuda.irconnect.IRConnection;
import org.rosuda.util.process.ProcessService;
import org.rosuda.util.process.RUNSTATE;
import org.rosuda.util.r.impl.RStarterFactory;

public class RStarterTest extends TestCase {

	public void testRun() {
		final ProcessService<IRConnection> service = new RStarterFactory().createService();
		assertNotNull(service);
		service.start();
		assertEquals(RUNSTATE.RUNNING, service.getRunState());
	}
}
