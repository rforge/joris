package org.rosuda.util.r.impl;

import org.rosuda.irconnect.IRConnection;
import org.rosuda.rengine.REngineConnectionFactory;
import org.rosuda.util.process.ProcessStopper;
import org.rosuda.util.process.RUNSTATE;
import org.rosuda.util.process.RunStateHolder;
import org.rosuda.util.process.RunstateAware;

class DefaultRStopper extends RunstateAware<IRConnection> implements ProcessStopper<IRConnection> {

	DefaultRStopper(final RunStateHolder<IRConnection> runStateHolder) {
		super(runStateHolder);
	}
	
	@Override
	public void stop() {
		final IRConnection rcon = REngineConnectionFactory.getInstance().createRConnection(null);
		if (rcon!=null) {
			rcon.shutdown();
		}
		runStateHolder.setRunState(RUNSTATE.TERMINATED);
	}

}
