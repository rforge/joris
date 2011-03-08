package org.rosuda.mapper.irexp.test;

import java.util.Properties;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rosuda.irconnect.ITwoWayConnection;
import org.rosuda.mapper.filter.ObjectTransformationManager;
import org.rosuda.mapper.irexp.IREXPMapper;
import org.rosuda.rengine.REngineConnectionFactory;
import org.rosuda.type.impl.NodeBuilderFactory;

public class RRestructureTest extends AbstractRTestCase{

	private static final Logger logger = Logger.getLogger(RFilterTest.class.getCanonicalName());
	private ITwoWayConnection connection;
	private ObjectTransformationManager<Object> filterMgr;
	
	@Before
	public void setUp() throws Exception {
		this.filterMgr = new ObjectTransformationManager<Object>(new NodeBuilderFactory<Object>(), 
				new IREXPMapper<Object>().createInstance());
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
