package org.rosuda.util.db;

import java.io.IOException;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.rosuda.util.process.AbstractMaxTimeoutProcessProvider;
import org.rosuda.util.process.ProcessStarter;
import org.rosuda.util.process.RUNSTATE;
import org.rosuda.util.process.RunStateHolder;

public class DatabaseStarter implements ProcessStarter<DataSource>{

	private final DerbyContext context;
	private final RunStateHolder<DataSource> runStateHolder;
	
	DatabaseStarter(final RunStateHolder<DataSource> runStateHolder, final DerbyContext context) {
		this.runStateHolder = runStateHolder;
		this.context = context;
	}
	
	@Override
	public void start() {	
		try {
			//try to get connection - if this works the service has already been started. Else start service
			if (!context.canCreateConnection()) {
				final Process process = context.processStartScript();
				final DataSource dataSource = new RetryStarter().create(process, runStateHolder, true);
				if (dataSource == null) {
					throw new IllegalStateException("could not start DerbyDB, please check your environment and user rights!");
				}
				context.setDataSource(dataSource);
			}
			this.runStateHolder.setRunState(RUNSTATE.RUNNING);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private class RetryStarter extends AbstractMaxTimeoutProcessProvider<DataSource> {

		private RetryStarter() {
			super.setMaxtimeout(10000l);
			super.setPolltime(500l);
		}
		@Override
		protected DataSource createResultInstance() {
			if (DatabaseStarter.this.context.canCreateConnection()) {
				try {
					return context.getDataSource();
				} catch (SQLException e) {
				}
			}
			return null;
		}
		
	}
}
