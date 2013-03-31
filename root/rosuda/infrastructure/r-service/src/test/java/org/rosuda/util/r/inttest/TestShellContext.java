package org.rosuda.util.r.inttest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.rosuda.util.process.ShellContext;

public class TestShellContext extends ShellContext {

    private Map<String, String> map = new HashMap<String, String>();
    private boolean onlyInternalEnv = false;

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    public void setProperty(final String propertyName, final String propertyValue) {
        this.map.put(propertyName, propertyValue);
    }

    public void setOnlyInternalEnv(boolean onlyInternalEnv) {
        this.onlyInternalEnv = onlyInternalEnv;
    }

    @Override
    public String getProperty(String propertyName) {
        final String propertyValue = map.get(propertyName);
        if (propertyValue != null) {
            return propertyValue;
        }
        return super.getProperty(propertyName);
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
