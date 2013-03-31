package org.rosuda.util.process;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ProcessContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessContext.class);

    protected static final String ENVIRONMENT_PREFIX = "JORIS_";

    private Runtime runtime = Runtime.getRuntime();

    public void setRuntime(final Runtime runtime) {
        this.runtime = runtime;
    }

    /**
     * creates an external process
     * 
     * @param runtimeArgs
     * @return
     * @throws IOException
     */
    public Process createProcessForArgs(final String[] runtimeArgs) throws IOException {
        final Process process = runtime.exec(runtimeArgs);
        createShutDownHook(process, runtimeArgs);
        return process;
    }

    /**
     * creates an external Process
     * 
     * @param startCommand
     * @return
     * @throws IOException
     */
    public Process createProcessForArg(String startCommand) throws IOException {
        final Process process = runtime.exec(startCommand);
        createShutDownHook(process, new String[] { startCommand });
        return process;
    }

    private void createShutDownHook(final Process process, final String[] args) {
        final StringBuilder processIdBuilder = new StringBuilder();
        for (final String arg : args) {
            processIdBuilder.append(arg).append(" ");
        }
        final String processId = processIdBuilder.toString().trim();
        final Thread shutDownThread = new Thread(new ShutdownRuntimeProcess(process, processId));
        shutDownThread.setName(this.getClass().getName() + "$" + ShutdownRuntimeProcess.class.getSimpleName() + "-" + processId);
    }

    public boolean stop() {
        return false;
    }

    private static class ShutdownRuntimeProcess implements Runnable {

        private final Process process;
        private final String processId;

        private ShutdownRuntimeProcess(final Process process, final String processId) {
            this.process = process;
            this.processId = processId;
        }

        @Override
        public void run() {
            LOGGER.info("shutting down Process \"" + processId + "\"");
            this.process.destroy();
        }

    }
}
