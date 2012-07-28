package org.rosuda.util.db;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rosuda.util.process.HasRunState;
import org.rosuda.util.process.ProcessFactory;
import org.rosuda.util.process.ProcessStarter;
import org.rosuda.util.process.ProcessStopper;
import org.rosuda.util.process.RunStateHolder;
import org.rosuda.util.process.RunningInstance;
import org.springframework.beans.factory.annotation.Required;

public class DatabaseStarterFactory extends ProcessFactory<DataSource> {

	private static final Log LOG = LogFactory
			.getLog(DatabaseStarterFactory.class);
	private final RunStateHolder<DataSource> runStateHolder = new RunStateHolder<DataSource>();

	private DerbyContext context = new DerbyContext();

	public DerbyContext getContext() {
		return context;
	}

	@Required
	public void setContext(final DerbyContext context) {
		this.context = context;
	}

	@Override
	protected ProcessStarter<DataSource> createStarter() {
		try {
			final DataSource dataSource = context.getDataSource();
			if (dataSource != null) {
				return new RunningInstance<DataSource>(runStateHolder);
			} 
		} catch (final SQLException sqlException) {
			LOG.error("Database not ready", sqlException);
		}
		return new DatabaseStarter(runStateHolder, context);
	}

	@Override
	protected ProcessStopper<DataSource> createStopper() {
		return new DatabaseStopper(runStateHolder, context);
	}

	@Override
	protected HasRunState<DataSource> createHasRunState() {
		return runStateHolder;
	}
}
