package org.rosuda.irconnect;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.rosuda.rengine.REngineConnectionFactory;

/**
 *
 * @author Ralf
 */
public class TestCreateConnection extends AbstractRTestCase{

	private Log log = LogFactory.getLog(TestCreateConnection.class);
		
	@Test
    public void testCreateREngineConnection() {
    	try {
    		final IRConnection irConnection = new REngineConnectionFactory().createRConnection(null);
    		Assert.assertNotNull(irConnection);
    		irConnection.close();
    	} catch (final RServerException x) {
    		log.warn("no r connection available - skipping integration test "+TestCreateConnection.class);
    	}
    }

}
