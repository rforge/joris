package org.rosuda.mapper.irexp.test.inttest;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.rosuda.irconnect.IRConnection;
import org.rosuda.util.process.ProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/spring/r-service.spring.xml"})
@Configurable
public abstract class AbstractRIntegrationTest {
	
	public static final double EPS = 0.000001;
	
	private ProcessService<IRConnection> service;
		
	@Autowired
	@Qualifier("rStarterProcess")
	public void setService(ProcessService<IRConnection> service) {
		this.service = service;
	}

	@Before
	public void startRService() throws Exception {
		service.start();
	}
	
	@After
	public void stopRService() throws Exception {
		service.stop();
	}

}
