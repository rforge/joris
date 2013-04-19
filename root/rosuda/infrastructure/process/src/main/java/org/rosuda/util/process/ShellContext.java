package org.rosuda.util.process;

import java.util.Map;

import org.rosuda.util.java.ClassPathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShellContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShellContext.class);
    
    public String getClasspath() {
        return ClassPathUtil.getLibrariesAsClassPathString();
    }

    public String getEnvironmentVariable(final String propertyName) {
        final String property = System.getenv(propertyName);
        if (property == null || property.trim().length() == 0) {
            return null;
        }
        return property;
    }

    public Map<String, String> getEnvironment() {
        return System.getenv();
    }

    public String getSystemProperty(final String propertyName) {
        return System.getProperty(propertyName);
    }
    
    public void setSystemProperty(final String propertyName, final String value) {
        LOGGER.info(">>>setting System property \""+propertyName+"\" to \""+value+"\".");
        if (value == null) {
            System.clearProperty(propertyName);
        } else {
            System.setProperty(propertyName, value);
        }
    }
}
