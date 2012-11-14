package org.rosuda.util.r.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rosuda.irconnect.IRConnection;
import org.rosuda.irconnect.RServerException;
import org.rosuda.util.process.ProcessStopper;
import org.rosuda.util.process.RUNSTATE;
import org.rosuda.util.process.RunStateHolder;
import org.rosuda.util.process.RunstateAware;

class DefaultRStopper extends RunstateAware<IRConnection> implements ProcessStopper<IRConnection> {

    private final RStartContext context;
    private final static Log log = LogFactory.getLog(DefaultRStopper.class);

    DefaultRStopper(final RunStateHolder<IRConnection> runStateHolder, final RStartContext setup) {
	super(runStateHolder);
	this.context = setup;
    }

    @Override
    public void stop() {
	try {
	    log.info("shutting down R-ConnectionFactory");
	    context.connectionFactory.shutdown();
	    log.info("R-ConnectionFactory has been shut down.");
	} catch (final RServerException rse) {
	    log.error("rserve shutdown failed", rse);
	} finally {
	    context.stop();
	    runStateHolder.setRunState(RUNSTATE.TERMINATED);
	}
    }

}
