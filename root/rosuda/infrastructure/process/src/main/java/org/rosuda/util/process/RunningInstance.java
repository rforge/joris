package org.rosuda.util.process;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RunningInstance<T> extends RunstateAware<T> implements ProcessStarter<T> {

	public RunningInstance(final RunStateHolder<T> runStateHolder) {
		super(runStateHolder);
		runStateHolder.setRunState(RUNSTATE.RUNNING);
	}

	private static final Log log = LogFactory.getLog(RunningInstance.class);

	
	@Override
	public void start() {
		log.info("reusing running instance.");
	}
	
}
