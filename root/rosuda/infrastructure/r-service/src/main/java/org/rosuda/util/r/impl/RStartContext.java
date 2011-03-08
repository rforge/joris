package org.rosuda.util.r.impl;

import java.util.Properties;

import org.rosuda.irconnect.IConnectionFactory;
import org.rosuda.irconnect.IRConnection;
import org.rosuda.util.process.ProcessContext;
import org.springframework.beans.factory.annotation.Required;

public class RStartContext extends ProcessContext{

	private Properties connectionProps;
	
	IConnectionFactory connectionFactory;
	
	@Required
	public void setConnectionFactory(final IConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}
	
	/**
	 * setter for the connection factory, the spring friendly way
	 * @param connectionProps
	 */
	public void setConnectionProps(final Properties connectionProps) {
		this.connectionProps = connectionProps;
	}
	
	/**
	 * creates a connection according to the setup
	 * @return
	 */
	public IRConnection createConnection() {
		return connectionFactory.createRConnection(connectionProps);
	}
	
}
