package org.rosuda.util.db;

import java.io.IOException;
import java.sql.Connection;

import org.rosuda.util.process.ProcessStarter;
import org.rosuda.util.process.RUNSTATE;
import org.rosuda.util.process.RunStateHolder;

public class DatabaseStarter implements ProcessStarter<Connection>{

	private final DerbyContext context;
	private final RunStateHolder<Connection> runStateHolder;
	
	DatabaseStarter(final RunStateHolder<Connection> runStateHolder, final DerbyContext context) {
		this.runStateHolder = runStateHolder;
		this.context = context;
	}
	
	@Override
	public void start() {	
		try {
			context.processStartScript();
			this.runStateHolder.setRunState(RUNSTATE.RUNNING);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}
	
}
