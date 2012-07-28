package org.rosuda.util.db;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rosuda.util.process.ProcessService;
import org.rosuda.util.process.RUNSTATE;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author Ralf Seger
 * 
 * this class uses the admin connection and creates the user
 *
 */
public class DatabaseMgr {

	private static final Log LOG = LogFactory.getLog(DatabaseMgr.class);
	private DatabaseStarterFactory factory;
	private ProcessService<DataSource> service;
	
	@Autowired
	public void setDatabaseStarterFactory(final DatabaseStarterFactory factory) {
		this.factory = factory;
	}
	
	@Autowired
	public void setService(final ProcessService<DataSource> service) {
		this.service = service;
	}
	
	public DataSource createDataSource(final DataSourceConfiguration userProperties) throws SQLException {
		LOG.debug("getUserDataSource()");
		if (this.service.getRunState()!=RUNSTATE.RUNNING) {
			LOG.debug("getUserDataSource().service.start()");
			this.service.start();
		}
		LOG.debug("getUserDateSource() has been started.");
		return factory.getContext().getDataSource();
	}
	
}
