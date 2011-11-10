package org.rosuda.irconnect.mgr;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

	private static final Log LOG = LogFactory.getLog(IRConnectionMgrImpl.class);

	@Autowired
	@Qualifier("rStarterProcess")
	private ProcessService<IRConnection> service;

	public void setService(final ProcessService<IRConnection> service) {
		this.service = service;
	}
	
	@Autowired
	@Qualifier("rConnectionFactory")
	private IConnectionFactory factory;
	
	public void setFactory(final IConnectionFactory factory) {
		this.factory = factory;
	}
	
	@Autowired
	@Qualifier("rConnectionConfiguration")
	private Properties configuration = new Properties();

	public void setConfiguration(Properties configuration) {
		this.configuration = configuration;
	}
	
	protected IRConnection createConnection() {
		LOG.debug("createConnection()");
		if (this.service.getRunState()!=RUNSTATE.RUNNING) {
			LOG.debug("createConnection().service.start()");
			this.service.start();
		}
		LOG.debug("createConnection().createTwoWayConnection("+configuration+")");
		return factory.createTwoWayConnection(configuration);
	}
	
	private IRConnection managedConnection;
	
	@Override
	public IRConnection getIRConnection(final IRConnectionConfig config) {
		LOG.debug("getIRConnection("+config+")");
		if (managedConnection == null)
			managedConnection = createConnection();
		for (final IRConnectionConfigStep step: config.getSteps()) {
			step.doWithConnection(managedConnection);
		}
		return managedConnection;
	}
}
