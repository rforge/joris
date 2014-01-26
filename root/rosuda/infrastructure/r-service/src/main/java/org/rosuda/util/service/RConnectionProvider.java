package org.rosuda.util.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.Callable;

import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.util.logging.LogMode;
import org.rosuda.util.logging.StreamLogger;
import org.rosuda.util.process.OS;
import org.rosuda.util.service.rserve.RServeService;
import org.rosuda.util.service.rserve.WindowsRServeService;
import org.rosuda.util.system.SystemContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RConnectionProvider implements ServiceProvider<RConnection> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RConnectionProvider.class);

    private class StartRByCommandProcess implements Callable<Integer> {

        private final String[] rServeArgs;

        private StartRByCommandProcess(String[] rServeArgs) {
            this.rServeArgs = rServeArgs;
        }

        @Override
        public Integer call() throws Exception {
            StreamLogger inputLogger = null;
            StreamLogger errorLogger = null;
            try {
                LOGGER.info("starting Rserve>" + Arrays.asList(rServeArgs));
                final Process rserve = Runtime.getRuntime().exec(rServeArgs);
                errorLogger = new StreamLogger(this.getClass(), "RServe>", LogMode.ERROR, rserve.getErrorStream());
                inputLogger = new StreamLogger(this.getClass(), "RServe>", LogMode.ERROR, rserve.getInputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);

            }
            return (int) (inputLogger.getLogLength() + errorLogger.getLogLength());
        }

    }

    @Override
    public boolean isReady(ServiceManager serviceManager) {
        final Collection<String> runningRserveProcesses = serviceManager.provide(SystemContext.class).runningProcesses("serv");
        int matchCount = 0;
        for (final String servProcess : runningRserveProcesses) {
            if (servProcess.toLowerCase().contains("rserve")) {
                matchCount++;
            }
        }
        return matchCount > 1;
    }

    @Override
    public void ready(ServiceManager serviceManager) {
        final RServeService rServiceService;
        // TODO der hier braucht auch schon ShellArgs wegen RServePort,
        // RSocket ...
        if (OS.isWindows()) {
            rServiceService = new WindowsRServeService(serviceManager.getShellContext());
        } else {
            rServiceService = null;
        }
        final String[] rserveStartCommand = rServiceService.getRuntimeArgs();
        Callable<Integer> startRByCommandProcess = this.new StartRByCommandProcess(rserveStartCommand);
        InTimeBeanFactory.provide(startRByCommandProcess);
    }

    @Override
    public RConnection provide(ServiceManager serviceManager) {

        // evt besser die Factory providen ?, auf jeden Fall brauch ich hier
        // irgendwo die ShellEnvironment wegen der Parameters
        // TODO Auto-generated method stub
        return null;
    }

}
