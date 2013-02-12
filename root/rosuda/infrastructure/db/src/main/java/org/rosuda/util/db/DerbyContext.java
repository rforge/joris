package org.rosuda.util.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rosuda.util.process.ProcessContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Required;

public class DerbyContext extends ProcessContext {

    private static final Log log = LogFactory.getLog(DerbyContext.class);

    private boolean processStartedByContext = false;
    private DataSource dataSource;
    private String derbyStarterProcess;
    private String derbyStopperProcess;
    private DataSourceConfiguration dataSourceConfiguration;

    void setDataSource(final DataSource dataSource) {
	this.dataSource = dataSource;
    }

    @Autowired
    @Required
    public void setDataSourceConfiguration(final DataSourceConfiguration config) {
	this.dataSourceConfiguration = config;
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

    public DataSource getDataSource() throws SQLException {
	return dataSource;
    }

    Process processStartScript() throws IOException {
	final String classPath = System.getProperty("java.class.path");
	final String startScript = MessageFormat.format(derbyStarterProcess, classPath, dataSourceConfiguration.getPort());
	log.info("starting database by command:\r\n>" + startScript);
	final Process process = createProcessForArg(startScript);
	processStartedByContext = process != null;
	return process;
    }

    @Override
    public boolean stop() {
	return processStartedByContext;
    }

    void processStopScript() throws IOException {
	final String classPath = System.getProperty("java.class.path");
	final String startScript = MessageFormat.format(derbyStopperProcess, classPath, dataSourceConfiguration.getPort());
	log.info("starting database by command:\r\n>" + startScript);
	createProcessForArg(startScript);
    }

    private DataSource fromConfig(DataSourceConfiguration userProperties) {
	BasicDataSource basicDataSource = new BasicDataSource();
	userProperties.getPort();
	basicDataSource.setDriverClassName(userProperties.getDriverClassName());
	basicDataSource.setUrl(userProperties.getUrl());
	basicDataSource.setUsername(userProperties.getUsername());
	basicDataSource.setPassword(userProperties.getPassword());
	return basicDataSource;
    }

    boolean canCreateConnection() {
	if (dataSource != null) {
	    return canCreateConnectionFromDataSource(dataSource);
	} else if (dataSourceConfiguration != null) {
	    final DataSource ds = fromConfig(dataSourceConfiguration);
	    if (canCreateConnectionFromDataSource(ds)) {
		dataSource = ds;
		log.info("datasource configured. ready to accept connections");
		return true;
	    }
	}
	return false;
    }

    private boolean canCreateConnectionFromDataSource(final DataSource aDataSource) {
	Connection con = null;
	try {
	    con = aDataSource.getConnection();
	} catch (final SQLException sqlX) {
	    log.warn("no db connection available", sqlX);
	} finally {
	    if (con != null) {
		try {
		    con.close();
		} catch (SQLException e) {
		    log.error(e);
		}
	    }
	}
	return con != null;
    }

}
