package org.rosuda.util.process;

public class ShellContext {
   
    public String getClasspath() {
	return System.getProperty("java.class.path");
    }
    
    public String getProperty(final String propertyName) {
	return System.getenv(propertyName);
    }

}
