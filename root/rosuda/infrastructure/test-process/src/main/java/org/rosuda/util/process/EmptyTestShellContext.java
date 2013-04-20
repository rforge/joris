package org.rosuda.util.process;

public class EmptyTestShellContext extends TestShellContext {

    @Override
    public String getEnvironmentVariable(String propertyName) {
        return null;
    }
}
