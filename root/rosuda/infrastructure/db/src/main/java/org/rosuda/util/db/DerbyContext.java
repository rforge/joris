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
import org.rosuda.util.process.ShellContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Required;

public class DerbyContext extends ProcessContext {

    private static final Log log = LogFactory.getLog(DerbyContext.class);

    public static final String DERBY_OPTS = "DERBY_OPTS";

    private boolean processStartedByContext = false;
    private DataSource dataSource;
    private String derbyStarterProcess;
    private String derbyStopperProcess;
    private DataSourceConfiguration dataSourceConfiguration;
    private ShellContext shellContext;
    
    void setDataSource(final DataSource dataSource) {
	this.dataSource = dataSource;
    }

    @Autowired
    @Required
    public void setShellContext(ShellContext shellContext) {
	this.shellContext = shellContext;
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
	final String startScript = createShellCall(derbyStarterProcess);
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
	final String startScript = createShellCall(derbyStopperProcess);
	log.info("starting database by command:\r\n>" + startScript);
	createProcessForArg(startScript);
    }

    protected String createShellCall(final String templateString) {
	String derbyOpts = shellContext.getProperty(DERBY_OPTS);
	if (derbyOpts == null)
	    derbyOpts = "";
	return MessageFormat.format(templateString, shellContext.getClasspath(), dataSourceConfiguration.getPort(), derbyOpts);
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
