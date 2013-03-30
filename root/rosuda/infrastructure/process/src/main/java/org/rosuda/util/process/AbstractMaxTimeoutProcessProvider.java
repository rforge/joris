package org.rosuda.util.process;

import org.rosuda.util.logging.LogMode;
import org.rosuda.util.logging.StreamLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 
 * @author RSE
 * 
 * 
 *         this class tries to create a process subject to a max timeout
 * @param <T>
 */
public abstract class AbstractMaxTimeoutProcessProvider<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractMaxTimeoutProcessProvider.class);

    private long maxtimeout = 5000;
    private long polltime = 3000;
    private StreamLogger inputLogger;
    private StreamLogger errorLogger;
    private ProcessMonitor processMonitor;
    private boolean isBlocking;
    private String processId;

    public void setMaxtimeout(long maxtimeout) {
        this.maxtimeout = maxtimeout;
    }

    public void setPolltime(long polltime) {
        this.polltime = polltime;
    }

    public T create(final Process process, final String processId, final RunStateHolder<T> runstateHolder, boolean isBlocking) {
        this.processId = processId;
        errorLogger = new StreamLogger(this.getClass(), "RServe>", LogMode.ERROR, process.getErrorStream());
        startNamedThread(errorLogger, "errorLogger");
        inputLogger = new StreamLogger(this.getClass(), "RServe>", LogMode.INFO, process.getInputStream());
        startNamedThread(inputLogger, "inputLogger");
        processMonitor = new ProcessMonitor(runstateHolder, process);
        this.isBlocking = isBlocking;
        startNamedThread(processMonitor, "processMonitor");
        if (!isBlocking) {
            try {
                process.waitFor();
            } catch (InterruptedException e) {
                LOGGER.debug("process was interrupted.");
            }
        }
        long totalTimeOut = 0;
        T result = null;
        while (result == null && totalTimeOut < maxtimeout) {
            synchronized (this) {
                try {
                    this.wait(polltime);
                    try {
                        result = createResultInstance();
                        // rcon =
                    } catch (final Exception x) {
                    }
                    totalTimeOut += polltime;
                } catch (InterruptedException e) {
                    LOGGER.debug("woke up");
                }
            }
        }
        return result;
    }

    private void startNamedThread(final Runnable runnable, final String id) {
        final Thread thread = new Thread(runnable);
        thread.setName(this.getClass().getName() + "-" + processId + "-" + id);
        thread.start();
    }

    protected abstract T createResultInstance();

    private class ProcessMonitor implements Runnable {

        private final RunStateHolder<T> runStateHolder;
        private final Process process;

        ProcessMonitor(final RunStateHolder<T> runStateHolder, final Process process) {
            this.runStateHolder = runStateHolder;
            this.process = process;
        }

        @Override
        public void run() {
            try {
                if (!isBlocking) {
                    final int exitCode = process.waitFor();
                    LOGGER.info("R process exited : " + exitCode);
                }
            } catch (InterruptedException e) {
                LOGGER.error("process was forcefully killed.");
            }
            runStateHolder.setRunState(RUNSTATE.TERMINATED);
        }

    }
}
