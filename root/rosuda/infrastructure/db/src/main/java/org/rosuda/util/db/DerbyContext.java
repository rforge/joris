package org.rosuda.util.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rosuda.util.process.ProcessContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class DerbyContext extends ProcessContext {

	private static final Log log = LogFactory.getLog(DerbyContext.class);
	
	private DriverManagerDataSource driverManagedDataSource;
	private String derbyStarterProcess;
	private String derbyStopperProcess;
	
	@Autowired
	@Required
	public void setDriverManagedDataSource(
			final DriverManagerDataSource driverManagedDataSource) {
		this.driverManagedDataSource = driverManagedDataSource;
	}
	
	@Autowired
	@Required
	@Qualifier("derbyStartCommand")
	public void setDerbyStarterProcess(final String derbyStarterProcess) {
		this.derbyStarterProcess = derbyStarterProcess;
	}
	
	@Autowired
	@Required
	@Qualifier("derbyStopCommand")
	public void setDerbyStopperProcess(final String derbyStopperProcess) {
		this.derbyStopperProcess = derbyStopperProcess;
	}
	
	public Connection getConnection() throws SQLException {
		return driverManagedDataSource.getConnection();
	}

	void processStartScript() throws IOException {
		final String classPath = System.getProperty("java.class.path");
		final String startScript = MessageFormat.format(derbyStarterProcess, classPath);
		log.info("starting database by command:\r\n>"+startScript);
		createProcessForArg(startScript);
	}
	
	void processStopScript() throws IOException {
		final String classPath = System.getProperty("java.class.path");
		final String startScript = MessageFormat.format(derbyStopperProcess, classPath);
		log.info("starting database by command:\r\n>"+startScript);
		createProcessForArg(startScript);
		
	}
	
}
