package org.rosuda.util.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.rosuda.util.process.ProcessContext;
import org.rosuda.util.process.ShellContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Required;

public class DerbyContext extends ProcessContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(DerbyContext.class);

    public static final String DERBY_HOST = "DERBY_HOST";
    public static final String DERBY_PORT = "DERBY_PORT";

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
        joinDataSourceConfigWithShellContext();
    }

    @Autowired
    @Required
    public void setDataSourceConfiguration(final DataSourceConfiguration config) {
        this.dataSourceConfiguration = config;
        joinDataSourceConfigWithShellContext();
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
        LOGGER.info("starting database by command:\r\n>" + startScript);
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
        LOGGER.info("starting database by command:\r\n>" + startScript);
        createProcessForArg(startScript);
    }

    protected String createShellCall(final String templateString) {
        StringBuilder derbyOpts = new StringBuilder();
        String derbyPort = shellContext.getEnvironmentVariable(DERBY_PORT);
        String derbyHost = shellContext.getEnvironmentVariable(DERBY_HOST);
        if (derbyPort != null) {
            derbyOpts.append(" -p ").append(derbyPort);
        } else {
            derbyOpts.append(" -p ").append(dataSourceConfiguration.getPort());
        }
        if (derbyHost != null) {
            derbyOpts.append(" -h ").append(derbyHost);
        }
        return MessageFormat.format(templateString, shellContext.getClasspath(), derbyOpts.toString());
    }

    private DataSource fromConfig(DataSourceConfiguration userProperties) {
        BasicDataSource basicDataSource = new BasicDataSource();
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
                LOGGER.info("datasource configured. ready to accept connections");
                return true;
            }
        }
        return false;
    }

    private void joinDataSourceConfigWithShellContext() {
        if (shellContext != null && dataSourceConfiguration != null) {
            dataSourceConfiguration.processEnvironmentConfiguration(shellContext);
        }
    }

    private boolean canCreateConnectionFromDataSource(final DataSource aDataSource) {
        Connection con = null;
        try {
            con = aDataSource.getConnection();
        } catch (final SQLException sqlX) {
            LOGGER.warn("no db connection available", sqlX);
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    LOGGER.error("failed to close datasource", e);
                }
            }
        }
        return con != null;
    }

}
