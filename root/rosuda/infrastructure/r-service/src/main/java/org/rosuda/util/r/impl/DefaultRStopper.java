package org.rosuda.util.r.impl;

import org.rosuda.irconnect.IRConnection;
import org.rosuda.util.process.ProcessStopper;
import org.rosuda.util.process.RUNSTATE;
import org.rosuda.util.process.RunStateHolder;
import org.rosuda.util.process.RunstateAware;

class DefaultRStopper extends RunstateAware<IRConnection> implements ProcessStopper<IRConnection> {

	private final RStartContext setup;
	
	DefaultRStopper(final RunStateHolder<IRConnection> runStateHolder, final RStartContext setup) {
		super(runStateHolder);
		this.setup = setup;
	}
	
	@Override
	public void stop() {
		setup.stop();
		final IRConnection rcon = setup.createConnection();
		if (rcon!=null) {
			rcon.shutdown();
		}
		runStateHolder.setRunState(RUNSTATE.TERMINATED);
	}

}
