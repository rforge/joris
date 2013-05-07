package org.rosuda.util.r.inttest;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.rosuda.irconnect.AConnectionFactory;
import org.rosuda.irconnect.IRConnection;
import org.rosuda.linux.socket.NativeSocketLibUtil;
import org.rosuda.rengine.REngineConnectionFactory;
import org.rosuda.util.process.ConstrainedShellContext;
import org.rosuda.util.process.OS;
import org.rosuda.util.process.ProcessService;
import org.rosuda.util.process.RUNSTATE;
import org.rosuda.util.process.ShellContext;
import org.rosuda.util.r.impl.RStartContext;
import org.rosuda.util.r.impl.RStarterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlainJavaStarterTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlainJavaStarterTest.class);
    private final int NUMBER_OF_DISTINCT_CONNECTIONS = 3;
    private static final double EPSILON = 1e-6;
    private static final long TIMEOUT = 600000;
    private AConnectionFactory rConnectionFactory;
    private Properties configuration;
    private ShellContext shellContext;
    private RStartContext startCtx;
    private ProcessService<IRConnection> rStarterService;
    private RStarterFactory rStarterFactory;

    @Before
    public void setUp() {
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

    @After
    public void tearDown() throws Exception {
        LOGGER.info("=============================================finishing===========================================================");
        if (RUNSTATE.RUNNING.equals(rStarterService.getRunState())) {
            rStarterService.stop();
        }
        killAllRProcesses();
        LOGGER.info("=============================================finished============================================================");
    }

    @Test
    public void stopKillsTheStartedRProcess() {
        LOGGER.info(">>RUNNING:stopKillsTheStartedRProcess()");
        rStarterService = rStarterFactory.createService();
        LOGGER.info("setUp test with runstate = "+rStarterService.getRunState());
        if (RUNSTATE.RUNNING.equals(rStarterService.getRunState())) {
            LOGGER.warn("rServe should be tested but is running from external resource, stop impossible!");
            return;
        }
        rStarterService.start();
        rStarterService.stop();
        assertThat(startCtx.getProcesses(), hasSize(greaterThanOrEqualTo(1)));
        int numberOfRunningProcesses = 0;
        for (Process proc : startCtx.getProcesses()) {
            if (verifyIfProcessIsRunning(proc)) {
                numberOfRunningProcesses++;
            }
        }
        assertThat(numberOfRunningProcesses, equalTo(0));
    }

    //TODO in test series fails sometimes
    @Ignore
    @Test
    public void theSocketPortIsAvailableAfterTheServiceIsStopped() {
        LOGGER.info(">>RUNNING:theSocketPortIsAvailableAfterTheServiceIsStopped()");
        if (OS.isWindows()) {
            // pass as domain socket is unsupported
            return;
        }
        final String host = shellContext.getEnvironment().get("JORIS_host");
        final int port = Integer.parseInt(shellContext.getEnvironment().get("JORIS_port"));

        rStarterService = rStarterFactory.createService();
        LOGGER.info("setUp test with runstate = "+rStarterService.getRunState());
        rStarterService.start();
        rStarterService.stop();
        try {
            final ServerSocket socketServer = new ServerSocket();
            socketServer.bind(new InetSocketAddress(host, port));
            socketServer.close();
        } catch (final IOException io) {
            fail("could not open server, port ist still blocked!");
        }
        
    }
    
    @Test
    public void oneConnectionThroughSocketPortWorks() {
        LOGGER.info(">>RUNNING:oneConnectionThroughSocketPortWorks()");       
        if (OS.isWindows()) {
            // pass as domain socket is unsupported
            return;
        }
        rStarterService = rStarterFactory.createService();
        LOGGER.info("setUp test with runstate = "+rStarterService.getRunState());
        rStarterService.start();
        IRConnection rConnection = rConnectionFactory.createRConnection(configuration);
        double result = rConnection.eval("1*2*3*5*8*13").asDouble();
        assertThat(result, closeTo(3120, EPSILON));
    }

    @Test(timeout = TIMEOUT)
    public void iCanCreateAsManyConnectionsUsingASocketStartAsILike() {
        LOGGER.info(">>RUNNING:iCanCreateAsManyConnectionsUsingASocketStartAsILike()");               
        if (OS.isWindows()) {
            // pass as domain socket is unsupported
            return;
        }
        rStarterService = rStarterFactory.createService();
        LOGGER.info("setUp test with runstate = "+rStarterService.getRunState());
        rStarterService.start();
        for (int i = 0; i < NUMBER_OF_DISTINCT_CONNECTIONS; i++) {
            LOGGER.info("creating rConnection #" + (1 + i));
            final IRConnection rConnection = rConnectionFactory.createRConnection(configuration);
            double result = rConnection.eval("1*2*3*5*8*13").asDouble();
            assertThat(result, closeTo(3120, EPSILON));
        }
    }

    @Test(timeout = TIMEOUT)
    public void connectionsThroughSocketSupportMultipleWorkspaces() {
        if (OS.isWindows()) {
            // pass as domain socket is unsupported
            return;
        }
        rStarterService = rStarterFactory.createService();
        LOGGER.info("setUp test with runstate = "+rStarterService.getRunState());
        rStarterService.start();
        final List<IRConnection> connections = new ArrayList<IRConnection>();
        for (int i = 0; i < NUMBER_OF_DISTINCT_CONNECTIONS; i++) {
            LOGGER.info("creating rConnection #" + (1 + i));
            final IRConnection rConnection = rConnectionFactory.createRConnection(configuration);
            connections.add(rConnection);
            rConnection.voidEval("x <- " + (i + 1));
        }
        final List<Integer> results = new ArrayList<Integer>();
        for (final IRConnection connection : connections) {
            results.add(connection.eval("x").asInt());
        }
        assertThat(results, hasItems(1, 2, 3));
    }
    
     // -- helper

    private boolean verifyIfProcessIsRunning(final Process process) {
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Integer> checkProcessIsRunning = executor.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return process.waitFor();
            }
        });
        try {
            checkProcessIsRunning.get(TIMEOUT, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            return true;
        } catch (Exception e) {
            return false;
        }
        return false;
    }
    
    private void killAllRProcesses() {
        try {
            Runtime.getRuntime().exec("pkill Rserve");
        } catch (IOException e) {
        }
    }
}
