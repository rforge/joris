package org.rosuda.util.process;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunningInstance<T> extends RunstateAware<T> implements ProcessStarter<T> {

    public RunningInstance(final RunStateHolder<T> runStateHolder) {
        super(runStateHolder);
        runStateHolder.setRunState(RUNSTATE.RUNNING);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(RunningInstance.class);

    @Override
    public void start() {
        LOGGER.info("reusing running instance.");
    }

}
