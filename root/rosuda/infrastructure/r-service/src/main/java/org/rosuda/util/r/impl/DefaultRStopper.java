package org.rosuda.util.r.impl;

import org.rosuda.irconnect.IRConnection;
import org.rosuda.irconnect.RServerException;
import org.rosuda.util.process.ProcessStopper;
import org.rosuda.util.process.RUNSTATE;
import org.rosuda.util.process.RunStateHolder;
import org.rosuda.util.process.RunstateAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class DefaultRStopper extends RunstateAware<IRConnection> implements ProcessStopper<IRConnection> {

    private final RStartContext context;
    private final static Logger LOGGER = LoggerFactory.getLogger(DefaultRStopper.class);

    DefaultRStopper(final RunStateHolder<IRConnection> runStateHolder, final RStartContext setup) {
        super(runStateHolder);
        this.context = setup;
    }

    @Override
    public void stop() {
        try {
            LOGGER.info("shutting down R-ConnectionFactory");
            context.connectionFactory.shutdown(context.getMergedConnectionProperties());
            LOGGER.info("R-ConnectionFactory has been shut down.");
        } catch (final RServerException rse) {
            LOGGER.error("rserve shutdown failed", rse);
        } finally {
            context.stop();
            runStateHolder.setRunState(RUNSTATE.TERMINATED);
        }
    }

}
