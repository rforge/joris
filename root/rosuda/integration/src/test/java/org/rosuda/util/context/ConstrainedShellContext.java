package org.rosuda.util.context;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.rosuda.util.process.ShellContext;

public class ConstrainedShellContext extends ShellContext {

    private final Properties systemProperties;
    private final Map<String, String> env;
    private final Set<String> allowedSystemPropertyNames;
    
    public ConstrainedShellContext(final Collection<String> allowedSystemPropertyNames) {
        env = new HashMap<String, String>(System.getenv());
        systemProperties = new Properties(System.getProperties());
        this.allowedSystemPropertyNames = Collections.unmodifiableSet(allowedSystemPropertyNames != null ? new TreeSet<String>(allowedSystemPropertyNames) : new TreeSet<String>()); 
    }

    @Override
    public String getEnvironmentVariable(String propertyName) {
        return env.get(propertyName);
    }

    @Override
    public Map<String, String> getEnvironment() {
        return env;
    }

    @Override
    public String getSystemProperty(String propertyName) {
        return systemProperties.getProperty(propertyName);
    }

    @Override
    public void setSystemProperty(String propertyName, String value) {
        if (allowedSystemPropertyNames.contains(propertyName)) {
            System.setProperty(propertyName, value);
        }
        systemProperties.setProperty(propertyName, value);
    }

    
}
