package org.rosuda.util.db;

import java.sql.Connection;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rosuda.util.process.ProcessService;
import org.rosuda.util.process.RUNSTATE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/spring/derby-service.spring.xml"})
@Configurable
public class DBStarterTest {

	private ProcessService<Connection> service;
	
	@Autowired
	@Qualifier("derbyStarterProcess")
	public void setService(ProcessService<Connection> service) {
		this.service = service;
	}

	//since this relies on a startDBScript (of some kind) maybe the whole idea needs second thoughts ?
	//the startCommand might be set inside the spring source alternatively

	@Test
	public void testRun() {
		Assert.assertNotNull(service);
		service.start();
		Assert.assertEquals(RUNSTATE.RUNNING, service.getRunState());
	}
	
	@Test
	public void testStop() {
		Assert.assertNotNull(service);
		service.start();
		Assert.assertEquals(RUNSTATE.RUNNING, service.getRunState());
		service.stop();
		Assert.assertEquals(RUNSTATE.TERMINATED, service.getRunState());
		
	}
}
