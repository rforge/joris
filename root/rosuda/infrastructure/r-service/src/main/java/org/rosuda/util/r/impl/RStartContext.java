package org.rosuda.util.r.impl;

import java.util.Map;
import java.util.Properties;

import org.rosuda.irconnect.IConnectionFactory;
import org.rosuda.irconnect.IRConnection;
import org.rosuda.irconnect.RServeOpts;
import org.rosuda.util.process.ProcessContext;
import org.rosuda.util.process.ShellContext;
import org.springframework.beans.factory.annotation.Required;

public class RStartContext extends ProcessContext {

    private Properties connectionProps;
    private ShellContext shellContext = new ShellContext();
    IConnectionFactory connectionFactory;

    public void setShellContext(ShellContext shellContext) {
        this.shellContext = shellContext;
    }

    @Required
    public void setConnectionFactory(final IConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    /**
     * setter for the connection factory, the spring friendly way
     * 
     * @param connectionProps
     */
    public void setConnectionProps(final Properties connectionProps) {
        this.connectionProps = connectionProps;
    }

    /**
     * creates a connection according to the setup
     * 
     * @return
     */
    public IRConnection createConnection() {
        final Properties mergedProperties = mergeConnectionPropertiesWithSystem(connectionProps);
        return connectionFactory.createRConnection(mergedProperties);
    }

    private Properties mergeConnectionPropertiesWithSystem(Properties properties) {
        if (properties == null) {
            properties = new Properties();
        }
        for (final Map.Entry<String, String> entry : shellContext.getEnvironment().entrySet()) {
            if (entry.getKey().toUpperCase().startsWith(ENVIRONMENT_PREFIX)) {
                final String key = entry.getKey().substring(6);
                properties.put(key, entry.getValue());
            }
        }
        final String environmentSocket = shellContext.getProperty(RServeOpts.SOCKET.getEnvironmentName());
        final String environmentPort = shellContext.getProperty(RServeOpts.PORT.getEnvironmentName());
        if (environmentSocket != null) {
            properties.setProperty(IConnectionFactory.SOCKET, environmentSocket);
        } else if (environmentPort != null) {
            properties.setProperty(IConnectionFactory.PORT, environmentPort);
        }
        return properties;
    }

    public ShellContext getShellContext() {
        return shellContext;
    }

    public Properties getMergedConnectionProperties() {
        return mergeConnectionPropertiesWithSystem(connectionProps);
    }

}
