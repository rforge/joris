package org.rosuda.irconnect;

import junit.framework.TestCase;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.rosuda.util.process.ProcessService;
import org.rosuda.util.r.impl.RStarterFactory;

public class AbstractRTestCase extends TestCase {

	private static ProcessService<IRConnection> starter;
	
	@BeforeClass
	protected void setUp() throws Exception {
		starter = new RStarterFactory().createService();
		starter.start();
	}
	
	@AfterClass
	protected void tearDown() throws Exception {
		starter.stop();
	}

}
