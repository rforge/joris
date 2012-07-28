package org.rosuda.mapper.irexp.test.inttest;

import java.util.Properties;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rosuda.irconnect.ITwoWayConnection;
import org.rosuda.rengine.REngineConnectionFactory;

public class RRestructureIntegrationTest extends AbstractRIntegrationTest{

	private ITwoWayConnection connection;
	
	@Before
	public void setUp() throws Exception {
		final Properties properties = new Properties();
		try {
			connection = REngineConnectionFactory.getInstance().createTwoWayConnection(properties);
		} catch (final Exception x) {
			x.printStackTrace();
		}
	}
	
	@After
	public void tearDown() throws Exception {
		connection.close();
	}
	
	@Test
	public void testRestructureRObject() {
		Assert.assertTrue(true);
		//TODO write a test
	}

}
