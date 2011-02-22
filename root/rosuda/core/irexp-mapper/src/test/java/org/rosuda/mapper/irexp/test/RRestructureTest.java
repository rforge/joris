package org.rosuda.mapper.irexp.test;

import java.util.Properties;
import java.util.logging.Logger;

import junit.framework.TestCase;

import org.rosuda.irconnect.ITwoWayConnection;
import org.rosuda.mapper.filter.ObjectTransformationManager;
import org.rosuda.mapper.irexp.IREXPMapper;
import org.rosuda.rengine.REngineConnectionFactory;
import org.rosuda.type.impl.NodeBuilderFactory;

public class RRestructureTest extends TestCase{

	private static final Logger logger = Logger.getLogger(RFilterTest.class.getCanonicalName());
	private ITwoWayConnection connection;
	private ObjectTransformationManager<Object> filterMgr;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.filterMgr = new ObjectTransformationManager<Object>(new NodeBuilderFactory<Object>(), 
				new IREXPMapper<Object>().createInstance());
		final Properties properties = new Properties();
		try {
			connection = REngineConnectionFactory.getInstance().createTwoWayConnection(properties);
		} catch (final Exception x) {
			x.printStackTrace();
		}
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		connection.close();
	}
	
	public void testRestructureRObject() {
		assertTrue(true);
		//TODO
	}

}
