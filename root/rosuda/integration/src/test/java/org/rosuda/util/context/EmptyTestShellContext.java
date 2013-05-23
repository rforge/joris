package org.rosuda.util.context;

public class EmptyTestShellContext extends TestShellContext {

    @Override
    public String getEnvironmentVariable(String propertyName) {
        return null;
    }
}
