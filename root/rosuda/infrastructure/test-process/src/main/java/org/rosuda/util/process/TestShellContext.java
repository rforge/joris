package org.rosuda.util.process;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class TestShellContext extends ShellContext {

    private Map<String, String> environment = new HashMap<String, String>();
    private Map<String, String> systemProperties = new HashMap<String, String>();
    private boolean fallbackAllowed = true;

    public TestShellContext() {
    }

    public TestShellContext(final Map<String, String> map) {
        this.environment = new HashMap<String, String>();
        initEnvironment(map);
        initSystemPropertis();
    }

    public TestShellContext(final Properties properties) {
        this.environment = new HashMap<String, String>();
        initEnvironment(properties);
        initSystemPropertis();
    }

    public void setEnvironmentProperty(final String propertyName, final String propertyValue) {
        this.environment.put(propertyName, propertyValue);
    }

    @Override
    public void setSystemProperty(String propertyName, String value) {
        systemProperties.put(propertyName, value);
    }

    @Override
    public String getSystemProperty(String propertyName) {
        return systemProperties.get(propertyName);
    }

    @Override
    public String getEnvironmentVariable(String propertyName) {
        final String propertyValue = environment.get(propertyName);
        if (propertyValue != null) {
            return propertyValue;
        }
        if (fallbackAllowed) {
            return super.getEnvironmentVariable(propertyName);
        } else {
            return null;
        }
    }

    @Override
    public Map<String, String> getEnvironment() {
        return Collections.unmodifiableMap(environment);
    }

    public void preventFallbackToSystemLookup() {
        this.fallbackAllowed = false;
    }

    // -- helper

    private void initEnvironment(Map<String, String> map) {
        this.environment.clear();
        this.environment.putAll(map);
        mergePropertiesIntoMap(map, System.getProperties());
    }

    private void initEnvironment(Properties properties) {
        this.environment.clear();
        mergePropertiesIntoMap(environment, properties);
        this.environment.putAll(System.getenv());
    }
    
    private void initSystemPropertis() {
        this.systemProperties.clear();
        mergePropertiesIntoMap(this.systemProperties, System.getProperties());
    }

    private void mergePropertiesIntoMap(Map<String, String> map, Properties properties) {
        for (final Object key : properties.keySet()) {
            final String keyString = (String) key;
            final String value = properties.getProperty(keyString);
            map.put(keyString, value);
        }
    }

}