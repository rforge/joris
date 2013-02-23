package org.rosuda.util.db;

import java.text.MessageFormat;

import org.rosuda.util.process.ShellContext;

public class DataSourceConfiguration {

    private String driverName;
    private String username;
    private String password;
    private String url;

    public String getDriverClassName() {
	return driverName;
    }

    public void setDriverClassName(String driverName) {
	this.driverName = driverName;
    }

    public String getUsername() {
	return username;
    }

    public void setUsername(String username) {
	this.username = username;
    }

    public String getPassword() {
	return password;
    }

    public void setPassword(String password) {
	this.password = password;
    }

    public String getUrl() {
	return url;
    }

    public void setUrl(final String url) {
	this.url = url;
    }

    public String getPort() {
	return url.split("\\:")[3].split("/")[0];
    }

    protected void processEnvironmentConfiguration(ShellContext shellContext) {
	final String hostFromEnvironment = shellContext.getProperty(DerbyContext.DERBY_HOST);
	String prefix = url.split("/")[0];
	final String portFromEnvironment = shellContext.getProperty(DerbyContext.DERBY_PORT);
	String urlHost = url.split("/")[2].split("\\:")[0];
	final String preconfiguredPort = getPort();
	String postfix = url.substring(url.indexOf(preconfiguredPort) + preconfiguredPort.length());
	String host = hostFromEnvironment != null ? hostFromEnvironment : urlHost;
	String port = portFromEnvironment != null ? portFromEnvironment : preconfiguredPort;
	this.url = MessageFormat.format("{0}//{1}:{2}{3}", prefix, host, port, postfix);
    }

}
