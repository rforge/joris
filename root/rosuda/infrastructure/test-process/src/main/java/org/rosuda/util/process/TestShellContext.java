package org.rosuda.util.process;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TestShellContext extends ShellContext {

    private Map<String, String> map = new HashMap<String, String>();
    private Map<String, String> systemProperties = new HashMap<String, String>();
    private boolean allowAccessToSystemEnv = false;
    private boolean allowAccessToSystemProps = false;

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    public void setProperty(final String propertyName, final String propertyValue) {
        this.map.put(propertyName, propertyValue);
    }

    @Override
    public void setSystemProperty(String propertyName, String value) {
        if (!allowAccessToSystemProps) {
            systemProperties.put(propertyName, value);
        } else {
            super.setSystemProperty(propertyName, value);
        }
    }

    @Override
    public String getSystemProperty(String propertyName) {
        if (!allowAccessToSystemProps) {
            return systemProperties.get(propertyName);
        } else {
            return super.getSystemProperty(propertyName);
        }
    }

    @Override
    public String getEnvironmentVariable(String propertyName) {
        final String propertyValue = map.get(propertyName);
        if (propertyValue != null) {
            return propertyValue;
        }
        if (!allowAccessToSystemEnv) {
            return null;
        }
        return super.getEnvironmentVariable(propertyName);
    }

    @Override
    public Map<String, String> getEnvironment() {
        if (!allowAccessToSystemEnv) {
            return Collections.unmodifiableMap(map);
        } else {
            return super.getEnvironment();
        }
    }

    /**
     * provides access to REAL System variables
     * 
     * @param onlyInternalEnv
     */
    public void enableSystemPropertyLookup() {
        this.allowAccessToSystemProps = true;
    }

    public void disableSystemPropertyLookup() {
        this.allowAccessToSystemProps = false;
    }

    /**
     * provides access to REAL System variables
     * 
     * @param onlyInternalEnv
     */
    public void enableSystemEnvironmentLookup() {
        this.allowAccessToSystemEnv = true;
    }

    public void disableSystemEnvironmentLookup() {
        this.allowAccessToSystemEnv = false;
    }
}