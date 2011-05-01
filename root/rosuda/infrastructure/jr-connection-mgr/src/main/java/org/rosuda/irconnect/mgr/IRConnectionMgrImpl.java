package org.rosuda.irconnect.mgr;

import java.util.Properties;

import org.rosuda.irconnect.IConnectionFactory;
import org.rosuda.irconnect.IRConnection;
import org.rosuda.irconnect.cfg.IRConnectionConfig;
import org.rosuda.irconnect.cfg.IRConnectionConfigStep;
import org.rosuda.util.process.ProcessService;
import org.rosuda.util.process.RUNSTATE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * manages a single r connection (with auto start) with respect to a given script
 * @author ralfseger
 *
 */
//reminder
//@ContextConfiguration(locations = {"classpath:/spring/r-service.spring.xml"})
public class IRConnectionMgrImpl implements IRConnectionMgr {

	private ProcessService<IRConnection> service;

	@Autowired
	@Qualifier("rStarterProcess")
	public void setService(ProcessService<IRConnection> service) {
		this.service = service;
	}
	
	@Autowired
	@Qualifier("rConnectionFactory")
	private IConnectionFactory factory;
	
	@Autowired
	@Qualifier("rConnectionConfiguration")
	private Properties configuration;
	
	protected IRConnection createConnection() {
		if (this.service.getRunState()!=RUNSTATE.RUNNING) {
			this.service.start();
		}
		return factory.createTwoWayConnection(configuration);
	}
	
	private IRConnection managedConnection;
	
	@Override
	public IRConnection getIRConnection(final IRConnectionConfig config) {
		if (managedConnection == null)
			managedConnection = createConnection();
		for (final IRConnectionConfigStep step: config.getSteps()) {
			step.doWithConnection(managedConnection);
		}
		return managedConnection;
	}
}
