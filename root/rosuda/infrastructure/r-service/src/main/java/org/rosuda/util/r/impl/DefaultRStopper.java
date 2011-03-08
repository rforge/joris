package org.rosuda.util.r.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rosuda.irconnect.IRConnection;
import org.rosuda.irconnect.RServerException;
import org.rosuda.util.process.ProcessStopper;
import org.rosuda.util.process.RUNSTATE;
import org.rosuda.util.process.RunStateHolder;
import org.rosuda.util.process.RunstateAware;

class DefaultRStopper extends RunstateAware<IRConnection> implements
		ProcessStopper<IRConnection> {

	private final RStartContext setup;
	private final static Log log = LogFactory.getLog(DefaultRStopper.class);

	DefaultRStopper(final RunStateHolder<IRConnection> runStateHolder,
			final RStartContext setup) {
		super(runStateHolder);
		this.setup = setup;
	}

	@Override
	public void stop() {

		try {
			final IRConnection rcon = setup.createConnection();
			if (rcon != null) {
				rcon.shutdown();
			}
		} catch (final RServerException rse) {
			log.error("rserve shutdown failed", rse);
		} finally {
			setup.stop();
			runStateHolder.setRunState(RUNSTATE.TERMINATED);
		}
	}

}
