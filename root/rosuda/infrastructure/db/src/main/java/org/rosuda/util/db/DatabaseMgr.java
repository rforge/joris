package org.rosuda.util.db;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.rosuda.util.process.ProcessService;
import org.rosuda.util.process.RUNSTATE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author Ralf Seger
 * 
 *         this class uses the admin connection and creates the user
 * 
 */
public class DatabaseMgr {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseMgr.class);
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
        LOGGER.debug("getUserDataSource()");
        if (this.service.getRunState() != RUNSTATE.RUNNING) {
            LOGGER.debug("getUserDataSource().service.start()");
            this.service.start();
        }
        LOGGER.debug("getUserDateSource() has been started.");
        return factory.getContext().getDataSource();
    }

}
