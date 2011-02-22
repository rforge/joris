package org.rosuda.util.db;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rosuda.util.process.HasRunState;
import org.rosuda.util.process.ProcessFactory;
import org.rosuda.util.process.ProcessStarter;
import org.rosuda.util.process.ProcessStopper;
import org.rosuda.util.process.RunStateHolder;
import org.rosuda.util.process.RunningInstance;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class DatabaseStarterFactory extends ProcessFactory<Connection> implements ApplicationContextAware{

	private final RunStateHolder<Connection> runStateHolder = new RunStateHolder<Connection>();
	private static final Log log = LogFactory.getLog(DatabaseStarterFactory.class);
	private DriverManagerDataSource driverManagerDataSource;
	private ApplicationContext applicationContext;
	
	public void setDriverManagerDataSource(
			final DriverManagerDataSource driverManagerDataSource) {
		this.driverManagerDataSource = driverManagerDataSource;
	}
	
	protected final void ensureDefaultSpringContext() {
		if (this.applicationContext == null) {
			log.info("loading default context");
			this.applicationContext = new ClassPathXmlApplicationContext("spring/db-start.spring.xml");
		}
	}
	
	protected DriverManagerDataSource getDriverManagerDataSource() {
		if (this.driverManagerDataSource == null) {
			ensureDefaultSpringContext();
			this.driverManagerDataSource = applicationContext.getBean("driverManagerDataSource", DriverManagerDataSource.class);
		}
		return this.driverManagerDataSource;
	}
		
	@Override
	protected ProcessStarter<Connection> createStarter() {
		try {
			final Connection con = getDriverManagerDataSource().getConnection();
			if (con != null)
				return new RunningInstance<Connection>(runStateHolder);
		} catch (final SQLException sqlException) {
		}
		return new DatabaseStarter(runStateHolder, this.applicationContext.getBean("processStartScript", String.class));
	}

	@Override
	protected ProcessStopper<Connection> createStopper() {
		return new DatabaseStopper();
	}

	@Override
	protected HasRunState<Connection> createHasRunState() {
		return runStateHolder;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}
}
