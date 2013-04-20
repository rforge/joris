package org.rosuda.util.process;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TestShellContext extends ShellContext {

    private Map<String, String> map = new HashMap<String, String>();
    private Map<String, String> systemProperties = new HashMap<String, String>();
    private boolean onlyInternalEnv = true;

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    public void setProperty(final String propertyName, final String propertyValue) {
        this.map.put(propertyName, propertyValue);
    }

    @Override
    public void setSystemProperty(String propertyName, String value) {
        if (onlyInternalEnv) {
            systemProperties.put(propertyName, value);
        } else {
            super.setSystemProperty(propertyName, value);
        }
    }

    @Override
    public String getSystemProperty(String propertyName) {
        if (onlyInternalEnv) {
            return systemProperties.get(propertyName);
        } else {
            return super.getSystemProperty(propertyName);
        }
    }

    /**
     * provides access to REAL System variables
     * 
     * @param onlyInternalEnv
     */
    public void setOnlyInternalEnv(boolean onlyInternalEnv) {
        this.onlyInternalEnv = onlyInternalEnv;
    }

    @Override
    public String getEnvironmentVariable(String propertyName) {
        final String propertyValue = map.get(propertyName);
        if (propertyValue != null) {
            return propertyValue;
        }
        return super.getEnvironmentVariable(propertyName);
    }

    @Override
    public Map<String, String> getEnvironment() {
        if (onlyInternalEnv) {
            return Collections.unmodifiableMap(map);
        } else {
            return super.getEnvironment();
        }
    }
}