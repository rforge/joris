package org.rosuda.util.process;

import java.util.Map;

import org.rosuda.util.java.ClassPathUtil;

public class ShellContext {

    public String getClasspath() {
        return ClassPathUtil.getLibrariesAsClassPathString();
    }

    public String getProperty(final String propertyName) {
        final String property = System.getenv(propertyName);
        if (property == null || property.trim().length() == 0) {
            return null;
        }
        return property;
    }

    public Map<String, String> getEnvironment() {
        return System.getenv();
    }

}
