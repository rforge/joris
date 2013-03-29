package org.rosuda.irconnect.inttest;

import java.util.Properties;

import org.rosuda.util.process.ShellContext;

public class TestShellContext extends ShellContext{

private Properties map = new Properties();
	
	public void setMap(Properties map) {
		this.map = map;
	}
	
	@Override
	public String getProperty(String propertyName) {
		final String propertyValue = map.getProperty(propertyName);
		if (propertyValue != null) {
			return propertyValue;
		}
		return super.getProperty(propertyName);
	}
}
