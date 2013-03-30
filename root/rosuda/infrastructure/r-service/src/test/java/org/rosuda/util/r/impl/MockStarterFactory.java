package org.rosuda.util.r.impl;

import org.rosuda.irconnect.IRConnection;
import org.rosuda.util.process.ProcessStarter;
import org.rosuda.util.process.RunStateHolder;

public class MockStarterFactory extends RStarterFactory {

    private ProcessStarter<IRConnection> mockFileRStarter;

    public RunStateHolder<IRConnection> getRunstateHolder() {
        return super.runStateHolder;
    }

    public void setStarter(ProcessStarter<IRConnection> mockFileRStarter) {
        this.mockFileRStarter = mockFileRStarter;
    }

    @Override
    protected ProcessStarter<IRConnection> handleCreateStarter() {
        return mockFileRStarter;
    }
}
