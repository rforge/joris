package org.rosuda.irconnect;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rosuda.rengine.REngineConnectionFactory;

/**
 *
 * @author Ralf
 */
public class TestCreateConnection extends TestCase{

	private Log log = LogFactory.getLog(TestCreateConnection.class);
	
    public void testCreateREngineConnection() {
    	try {
    		final IRConnection irConnection = new REngineConnectionFactory().createRConnection(null);
    		assertNotNull(irConnection);
    		irConnection.close();
    	} catch (final RServerException x) {
    		log.warn("no r connection available - skipping integration test "+TestCreateConnection.class);
    	}
    }

}
