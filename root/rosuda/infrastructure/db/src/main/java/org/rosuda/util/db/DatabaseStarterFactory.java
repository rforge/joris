package org.rosuda.util.db;

import java.sql.Connection;
import java.sql.SQLException;

import org.rosuda.util.process.HasRunState;
import org.rosuda.util.process.ProcessFactory;
import org.rosuda.util.process.ProcessStarter;
import org.rosuda.util.process.ProcessStopper;
import org.rosuda.util.process.RunStateHolder;
import org.rosuda.util.process.RunningInstance;
import org.springframework.beans.factory.annotation.Required;

public class DatabaseStarterFactory extends ProcessFactory<Connection> {

	private final RunStateHolder<Connection> runStateHolder = new RunStateHolder<Connection>();

	private DerbyContext context = new DerbyContext();
	
	public DerbyContext getContext() {
		return context;
	}

	@Required
	public void setContext(final DerbyContext context) {
		this.context = context;
	}

	
	@Override
	protected ProcessStarter<Connection> createStarter() {
		try {
			final Connection con = context.getConnection();
			if (con != null)
				return new RunningInstance<Connection>(runStateHolder);
		} catch (final SQLException sqlException) {
		}
		return new DatabaseStarter(runStateHolder, context);
	}

	@Override
	protected ProcessStopper<Connection> createStopper() {
		return new DatabaseStopper(runStateHolder, context);
	}

	@Override
	protected HasRunState<Connection> createHasRunState() {
		return runStateHolder;
	}
}
