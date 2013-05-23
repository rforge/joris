package org.rosuda.integration.suites.util;

import java.util.Arrays;
import java.util.Properties;

import org.rosuda.irconnect.AConnectionFactory;
import org.rosuda.irconnect.IRConnection;
import org.rosuda.irconnect.ITwoWayConnection;
import org.rosuda.linux.socket.NativeSocketLibUtil;
import org.rosuda.rengine.REngineConnectionFactory;
import org.rosuda.util.context.ConstrainedShellContext;
import org.rosuda.util.process.ProcessService;
import org.rosuda.util.process.RUNSTATE;
import org.rosuda.util.process.ShellContext;
import org.rosuda.util.r.impl.RStartContext;
import org.rosuda.util.r.impl.RStarterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlainJavaConnectionTestSuiteContext {

    protected static final Logger LOGGER = LoggerFactory.getLogger(PlainJavaConnectionTestSuiteContext.class);
    public static final double EPSILON = 1e-6;
    public static final long TIMEOUT = 600000;
    private AConnectionFactory rConnectionFactory;
    private Properties configuration;
    private ShellContext shellContext;
    private RStartContext startCtx;
    private ProcessService<IRConnection> rStarterService;
    private RStarterFactory rStarterFactory;

    private ITwoWayConnection testConnection;
    private static PlainJavaConnectionTestSuiteContext instance;

    protected PlainJavaConnectionTestSuiteContext() {

    }

    public static PlainJavaConnectionTestSuiteContext getInstance() {
        if (instance == null) {
            instance = new PlainJavaConnectionTestSuiteContext();
            instance.init();
        }
        return instance;
    }

    private void init() {
        configuration = new Properties();
        rConnectionFactory = new REngineConnectionFactory();
        startCtx = new RStartContext();
        startCtx.setConnectionFactory(rConnectionFactory);
        startCtx.setConnectionProps(configuration);
        shellContext = new ConstrainedShellContext(Arrays.asList(NativeSocketLibUtil.ENV_NATIVE_LIBRARY_PATH));
        startCtx.setShellContext(shellContext);
        if (shellContext.getEnvironmentVariable("JORIS_host") == null) {
            shellContext.getEnvironment().put("JORIS_host", "localhost");
        }
        if (shellContext.getEnvironmentVariable("JORIS_port") == null) {
            shellContext.getEnvironment().put("JORIS_port", "6311");
        }
        rStarterFactory = new RStarterFactory();
        rStarterFactory.setContext(startCtx);
    }

    public void shutdown() {
        try {
            if (RUNSTATE.RUNNING.equals(rStarterService.getRunState())) {
                rStarterService.stop();
            }
        } finally {
            instance = null;
        }
    }

    public IRConnection acquireRConnection() {
        return instance.getConnection();
    }

    public ITwoWayConnection acquireAssignableRConnection() {
        return instance.getConnection();
    }

    private ITwoWayConnection getConnection() {
        if (testConnection != null) {
            LOGGER.info("reusing testconnection");
            return testConnection;
        } else {
            LOGGER.info("initializing testConnection");
            rStarterService = rStarterFactory.createService();
            LOGGER.info("setUp test with runstate = " + rStarterService.getRunState());
            rStarterService.start();
            testConnection = rConnectionFactory.createTwoWayConnection(configuration);
            return testConnection;
        }
    }

}
