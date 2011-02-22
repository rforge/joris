package org.rosuda.util.r.impl;

import org.rosuda.irconnect.IRConnection;
import org.rosuda.rengine.REngineConnectionFactory;
import org.rosuda.util.process.HasRunState;
import org.rosuda.util.process.OS;
import org.rosuda.util.process.ProcessFactory;
import org.rosuda.util.process.ProcessStarter;
import org.rosuda.util.process.ProcessStopper;
import org.rosuda.util.process.RunStateHolder;
import org.rosuda.util.process.RunningInstance;

public class RStarterFactory extends ProcessFactory<IRConnection>{

	private final RunStateHolder<IRConnection> runStateHolder = new RunStateHolder<IRConnection>();
	
	protected ProcessStarter<IRConnection> createStarter() {
		try {
			final IRConnection rcon = REngineConnectionFactory.getInstance().createRConnection(null);
			rcon.close();
			return new RunningInstance<IRConnection>(runStateHolder);
		} catch (final Exception x) {}
		if (OS.isWindows()) {
			return new WindowsRStarter(runStateHolder);
		} else {
			return new UnixRStarter(runStateHolder);
		}
	}

	protected ProcessStopper<IRConnection> createStopper() {
		return new DefaultRStopper(runStateHolder);
	}

	protected HasRunState<IRConnection> createHasRunState() {
		return runStateHolder;
	}
}
