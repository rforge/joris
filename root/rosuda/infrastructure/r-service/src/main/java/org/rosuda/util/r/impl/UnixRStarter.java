package org.rosuda.util.r.impl;

import java.io.File;
import java.util.List;
import java.util.StringTokenizer;

import org.rosuda.irconnect.IRConnection;
import org.rosuda.util.process.RunStateHolder;

class UnixRStarter extends AbstractRStarter {

    final static String[] checkedLocations = new String[] { "/usr/local/lib/R/bin/R", "/usr/lib/R/bin/R", "/usr/local/bin/R", "/sw/bin/R",
            "/Library/Frameworks/R.Framework/Resources/bin/R", };

    UnixRStarter(RunStateHolder<IRConnection> runStateHolder, final RStartContext setup) {
        super(runStateHolder, setup);
    }

    @Override
    void initRFileLocations(final List<File> list) {
        final String environmentPath = setup.getShellContext().getEnvironment().get("PATH");
        if (environmentPath != null) {
            final StringTokenizer pathTokenizer = new StringTokenizer(environmentPath, File.pathSeparator, false);
            while (pathTokenizer.hasMoreTokens()) {
                final String path = pathTokenizer.nextToken();
                final File potentialRFile = new File(path, "R");
                LOGGER.debug("checking for potential R file in "+path);
                if (potentialRFile.exists()) {
                    LOGGER.info("found R file "+potentialRFile.getAbsolutePath());                    
                    list.add(potentialRFile);
                }
            }
        }
        for (final String location : checkedLocations) {
            final File rFile = new File(location);
            if (rFile.exists()) {
                list.add(rFile);
            }
        }
    }

    @Override
    String[] getRuntimeArgs(final String executableRFile) {
        return new String[] {
                "/bin/sh",
                "-c",
                "echo 'library(Rserve);Rserve(args=\"" + optionalEnvironmentArguments() + R_SERVE_ARGS + "\")' | " + executableRFile + " "
                        + R_ARGS };
    }

    @Override
    protected boolean isBlocking() {
        return false;
    }

}
