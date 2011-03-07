package org.rosuda.util.db;

import java.io.IOException;
import java.sql.Connection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rosuda.util.process.ProcessStopper;
import org.rosuda.util.process.RUNSTATE;
import org.rosuda.util.process.RunStateHolder;

public class DatabaseStopper implements ProcessStopper<Connection>{

	private final RunStateHolder<Connection> runstateHolder;
	private final DerbyContext context;
	
	DatabaseStopper(final RunStateHolder<Connection> runstateHolder, final DerbyContext context) {
		this.runstateHolder = runstateHolder;
		this.context = context;
	}
	
	private static final Log log = LogFactory.getLog(DatabaseStopper.class);
	@Override
	public void stop() {
		if (context.stop()) {
			//send stop command:
			try {
				context.processStopScript();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			runstateHolder.setRunState(RUNSTATE.TERMINATED);
		} else {
			log.warn("stop database was invoked, but no processs can be stopped. Fake set state to TERMINATED.");
			runstateHolder.setRunState(RUNSTATE.TERMINATED);
		}
	}

}
