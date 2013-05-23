package org.rosuda.irconnect.cfg;

import java.util.ArrayList;
import java.util.List;

public class IRConnectionConfigImpl implements IRConnectionConfig {

    private List<IRConnectionConfigStep> steps = new ArrayList<IRConnectionConfigStep>();

    public void setSteps(final List<IRConnectionConfigStep> steps) {
        this.steps.clear();
        if (steps != null) {
            this.steps.addAll(steps);
        }
    }

    @Override
    public List<IRConnectionConfigStep> getSteps() {
        return steps;
    }

}
