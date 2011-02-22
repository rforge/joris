package org.rosuda.util.db;

import java.sql.Connection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rosuda.util.process.ProcessStopper;

public class DatabaseStopper implements ProcessStopper<Connection>{

	private static final Log log = LogFactory.getLog(DatabaseStopper.class);
	@Override
	public void stop() {
		// TODO Auto-generated method stub, dummy currently
		log.info("stop database was invoked.");		
	}

}
