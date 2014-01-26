package org.rosuda.util.system;

import java.util.Collection;

public interface SystemContext {
    public Collection<String> runningProcesses(final String taskname);
}
