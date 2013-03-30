package org.rosuda.util.db;

import java.io.IOException;

import javax.sql.DataSource;

import org.rosuda.util.process.ProcessStopper;
import org.rosuda.util.process.RUNSTATE;
import org.rosuda.util.process.RunStateHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseStopper implements ProcessStopper<DataSource> {

    private final RunStateHolder<DataSource> runstateHolder;
    private final DerbyContext context;

    DatabaseStopper(final RunStateHolder<DataSource> runstateHolder, final DerbyContext context) {
        this.runstateHolder = runstateHolder;
        this.context = context;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseStopper.class);

    @Override
    public void stop() {
        if (context.stop()) {
            // send stop command:
            try {
                context.processStopScript();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            runstateHolder.setRunState(RUNSTATE.TERMINATED);
        } else {
            LOGGER.warn("stop database was invoked, but no processs can be stopped. Fake set state to TERMINATED.");
            runstateHolder.setRunState(RUNSTATE.TERMINATED);
        }
    }

}
