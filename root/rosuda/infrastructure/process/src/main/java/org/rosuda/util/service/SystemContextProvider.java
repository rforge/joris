package org.rosuda.util.service;

import org.rosuda.util.process.OS;
import org.rosuda.util.system.SystemContext;
import org.rosuda.util.system.UXSystemContext;
import org.rosuda.util.system.WindowsSystemContext;

public class SystemContextProvider implements ServiceProvider<SystemContext> {

    private SystemContext osSpecificContext = null;

    @Override
    public boolean isReady(ServiceManager serviceManager) {
        return osSpecificContext != null;
    }

    @Override
    public void ready(ServiceManager serviceManager) {
        if (OS.isWindows()) {
            osSpecificContext = new WindowsSystemContext();
        } else {
            osSpecificContext = new UXSystemContext();
        }

    }

    @Override
    public SystemContext provide(ServiceManager serviceManager) {
        return osSpecificContext;
    }

}
