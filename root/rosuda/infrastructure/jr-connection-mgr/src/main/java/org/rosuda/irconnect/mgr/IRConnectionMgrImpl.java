package org.rosuda.irconnect.mgr;

import java.util.Properties;

import org.rosuda.irconnect.IConnectionFactory;
import org.rosuda.irconnect.IRConnection;
import org.rosuda.irconnect.cfg.IRConnectionConfig;
import org.rosuda.irconnect.cfg.IRConnectionConfigStep;
import org.rosuda.util.java.RServeUtil;
import org.rosuda.util.process.OS;
import org.rosuda.util.process.ProcessService;
import org.rosuda.util.process.RUNSTATE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * manages a single r connection (with auto start) with respect to a given
 * script
 * 
 * @author ralfseger
 * 
 */
// reminder
// @ContextConfiguration(locations = {"classpath:/spring/r-service.spring.xml"})
public class IRConnectionMgrImpl implements IRConnectionMgr {

    static {
        ensureRServeIsDown();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(IRConnectionMgrImpl.class);

    private ProcessService<IRConnection> service;

    @Autowired
    @Qualifier("rStarterProcess")
    public void setService(final ProcessService<IRConnection> service) {
        this.service = service;
    }

    private static void ensureRServeIsDown() {
        if (OS.isWindows()) {
            RServeUtil.killAllWindowsRProcesses();
        } else {
            RServeUtil.killAllUXRProcesses();
        }

    }

    private IConnectionFactory factory;

    @Autowired
    @Qualifier("rConnectionFactory")
    public void setFactory(final IConnectionFactory factory) {
        this.factory = factory;
    }

    private Properties configuration = new Properties();

    @Autowired
    @Qualifier("rConnectionConfiguration")
    public void setConfiguration(Properties configuration) {
        this.configuration = configuration;
    }

    protected IRConnection createConnection() {
        // Fails on windows when another instance of Rserve.exe is running ...
        // (locked port 6311)
        LOGGER.debug("createConnection()");
        if (this.service.getRunState() != RUNSTATE.RUNNING) {
            LOGGER.debug("createConnection().service.start()");
            this.service.start();
        }
        LOGGER.debug("createConnection().createTwoWayConnection(" + configuration + ")");
        return factory.createTwoWayConnection(configuration);
    }

    private IRConnection managedConnection;

    @Override
    public IRConnection getIRConnection(final IRConnectionConfig config) {
        LOGGER.debug("getIRConnection(" + config + ")");
        if (managedConnection == null)
            managedConnection = createConnection();
        for (final IRConnectionConfigStep step : config.getSteps()) {
            step.doWithConnection(managedConnection);
        }
        return managedConnection;
    }
}
