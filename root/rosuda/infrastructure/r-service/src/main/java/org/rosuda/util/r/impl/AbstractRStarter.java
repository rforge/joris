package org.rosuda.util.r.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rosuda.irconnect.IRConnection;
import org.rosuda.irconnect.proxy.RConnectionProxy;
import org.rosuda.rengine.REngineConnectionFactory;
import org.rosuda.util.process.AbstractMaxTimeoutProcessProvider;
import org.rosuda.util.process.ProcessStarter;
import org.rosuda.util.process.RUNSTATE;
import org.rosuda.util.process.RunStateHolder;
import org.rosuda.util.process.RunstateAware;

abstract class AbstractRStarter extends RunstateAware<IRConnection> implements ProcessStarter<IRConnection> {

    protected final Log log = LogFactory.getLog(getClass());
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
	for (final File startFile : fileLocations) {
	    if (!startFile.exists()) {
		log.info("skipping configured but not present r start file \"" + startFile.getAbsolutePath() + "\"");
		continue;
	    }
	    log.info("starting R process for '" + startFile.getAbsolutePath() + "'");
	    try {
		final String[] runtimeArgs = getRuntimeArgs(startFile.getAbsolutePath());
		final StringBuilder sb = new StringBuilder().append("> ");
		for (final String arg : runtimeArgs) {
		    sb.append(arg);
		    sb.append(" ");
		}
		log.info(sb.toString());

		process = setup.createProcessForArgs(runtimeArgs);
		// append loggers
		final IRConnection rcon = new RetryStarter().create(process, this.getClass().getSimpleName(), runStateHolder, isBlocking());
		if (rcon != null) {
		    rcon.close();
		    runStateHolder.setRunState(RUNSTATE.RUNNING);
		    return;
		    // TODO kill process for maven ?
		} else {
		    log.warn("no answer from R - please make sure R is installed an Rserve running.");
		}
	    } catch (final IOException x) {
		log.fatal(x);
	    }
	}
    }

    protected abstract boolean isBlocking();

    private static final class RetryStarter extends AbstractMaxTimeoutProcessProvider<IRConnection> {

	@Override
	protected IRConnection createResultInstance() {
	    return RConnectionProxy.createProxy(REngineConnectionFactory.getInstance().createRConnection(null), null);
	}

    }
}
