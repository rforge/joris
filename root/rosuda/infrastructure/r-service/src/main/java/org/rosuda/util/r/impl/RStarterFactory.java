package org.rosuda.util.r.impl;

import org.rosuda.irconnect.IRConnection;
import org.rosuda.util.process.HasRunState;
import org.rosuda.util.process.OS;
import org.rosuda.util.process.ProcessFactory;
import org.rosuda.util.process.ProcessStarter;
import org.rosuda.util.process.ProcessStopper;
import org.rosuda.util.process.RunStateHolder;
import org.rosuda.util.process.RunningInstance;
import org.springframework.beans.factory.annotation.Required;


public class RStarterFactory extends ProcessFactory<IRConnection>{

	private final RunStateHolder<IRConnection> runStateHolder = new RunStateHolder<IRConnection>();
	
	private RStartContext context = new RStartContext();
		
	public RStartContext getContext() {
		return context;
	}

	@Required
	public void setContext(final RStartContext context) {
		this.context = context;
	}

	protected ProcessStarter<IRConnection> createStarter() {
		try {
			final IRConnection rcon = context.createConnection();
			rcon.close();
			return new RunningInstance<IRConnection>(runStateHolder);
		} catch (final Exception x) {}
		if (OS.isWindows()) {
			return new WindowsRStarter(runStateHolder, context);
		} else {
			return new UnixRStarter(runStateHolder, context);
		}
	}

	protected ProcessStopper<IRConnection> createStopper() {
		return new DefaultRStopper(runStateHolder, context);
	}

	protected HasRunState<IRConnection> createHasRunState() {
		return runStateHolder;
	}
}
