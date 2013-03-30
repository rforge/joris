package org.rosuda.util.r.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.rosuda.irconnect.IRConnection;
import org.rosuda.util.process.RunStateHolder;

public class MockFileRStarter extends AbstractRStarter {

    private boolean blocking;
    private List<File> additionalLocations;
    private String[] runtimeArgs = new String[] {};

    public MockFileRStarter(RunStateHolder<IRConnection> runStateHolder, RStartContext setup) {
        super(runStateHolder, setup);
        this.additionalLocations = new ArrayList<File>();
    }

    public void setBlocking(boolean blocking) {
        this.blocking = blocking;
    }

    public void setAdditionalLocations(List<File> additionalLocations) {
        this.additionalLocations = additionalLocations;
    }

    public void setRuntimeArgs(String[] runtimeArgs) {
        this.runtimeArgs = runtimeArgs;
    }

    @Override
    void initRFileLocations(List<File> list) {
        if (this.additionalLocations != null) {
            list.addAll(additionalLocations);
        }

    }

    @Override
    String[] getRuntimeArgs(String executableRFile) {
        return this.runtimeArgs;
    }

    @Override
    protected boolean isBlocking() {
        return this.blocking;
    }

}
