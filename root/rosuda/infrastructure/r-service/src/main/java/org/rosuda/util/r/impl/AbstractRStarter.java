package org.rosuda.util.r.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.rosuda.irconnect.IConnectionFactory;
import org.rosuda.irconnect.IRConnection;
import org.rosuda.irconnect.RServeOpts;
import org.rosuda.irconnect.proxy.RConnectionProxy;
import org.rosuda.util.process.AbstractMaxTimeoutProcessProvider;
import org.rosuda.util.process.ProcessStarter;
import org.rosuda.util.process.RUNSTATE;
import org.rosuda.util.process.RunStateHolder;
import org.rosuda.util.process.RunstateAware;
import org.rosuda.util.process.ShellContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class AbstractRStarter extends RunstateAware<IRConnection> implements ProcessStarter<IRConnection> {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
    protected final String R_ARGS = "--vanilla --slave";
    protected final String R_SERVE_ARGS = "--no-save --slave";
    protected final List<File> fileLocations = Collections.unmodifiableList(getRFileLocations());

    final RStartContext setup;
    private Process process;

    public AbstractRStarter(final RunStateHolder<IRConnection> runStateHolder, final RStartContext setup) {
        super(runStateHolder);
        this.setup = setup;
        runStateHolder.setRunState(RUNSTATE.STARTING);
    }

    private final List<File> getRFileLocations() {
        final List<File> list = new ArrayList<File>();
        // first choice:
        list.add(new File("R"));
        initRFileLocations(list);
        return Collections.unmodifiableList(list);
    }

    abstract void initRFileLocations(final List<File> list);

    abstract String[] getRuntimeArgs(final String executableRFile);

    @Override
    public final void start() {
        LOGGER.warn("****" + this.getClass() + "****AbstractRStarter.start() with fileLocations " + fileLocations);
        for (final File startFile : fileLocations) {
            if (!startFile.exists()) {
                LOGGER.info("skipping configured but not present r start file \"" + startFile.getAbsolutePath() + "\"");
                continue;
            }
            LOGGER.info("starting R process for '" + startFile.getAbsolutePath() + "'");
            try {
                final String[] runtimeArgs = getRuntimeArgs(startFile.getAbsolutePath());
                final StringBuilder sb = new StringBuilder().append("> ");
                for (final String arg : runtimeArgs) {
                    sb.append(arg);
                    sb.append(" ");
                }
                LOGGER.info(sb.toString());
                LOGGER.warn("****" + this.getClass() + "**** create process for Args " + runtimeArgs);
                process = setup.createProcessForArgs(runtimeArgs);
                // append loggers
                final IRConnection rcon = new RetryStarter(setup.connectionFactory, setup.getMergedConnectionProperties()).create(process,
                        this.getClass().getSimpleName(), runStateHolder, isBlocking());
                LOGGER.warn("****" + this.getClass() + "**** created process for Args " + runtimeArgs + " -> rcon = " + rcon);
                if (rcon != null) {
                    rcon.close();
                    runStateHolder.setRunState(RUNSTATE.RUNNING);
                    return;
                    // TODO kill process for maven ?
                } else {
                    LOGGER.warn("no answer from R - please make sure R is installed an Rserve running.");
                }
            } catch (final IOException x) {
                LOGGER.error("start() failed.", x);
            }
        }
    }

    protected abstract boolean isBlocking();

    protected String optionalEnvironmentArguments() {
        final ShellContext shellContext = setup.getShellContext();
        if (shellContext == null) {
            return "";
        }
        final StringBuilder envStringBuilder = new StringBuilder();
        for (RServeOpts rsOpt : RServeOpts.values()) {
            String option = shellContext.getProperty(rsOpt.getEnvironmentName());
            if (option != null && option.trim().length() > 0) {
                envStringBuilder.append(rsOpt.asRServeOption()).append(" ").append(option).append(" ");
            }

        }
        return envStringBuilder.toString();

    }

    private static final class RetryStarter extends AbstractMaxTimeoutProcessProvider<IRConnection> {

        private final IConnectionFactory factory;
        private final Properties connectionProperties;

        private RetryStarter(final IConnectionFactory factory, final Properties properties) {
            this.factory = factory;
            this.connectionProperties = properties;
        }

        @Override
        protected IRConnection createResultInstance() {
            return RConnectionProxy.createProxy(factory.createRConnection(connectionProperties), null);
        }

    }
}
