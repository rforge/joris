package org.rosuda.util.db;

import java.sql.Connection;

import org.junit.Assert;
import org.junit.Test;
import org.rosuda.util.process.ProcessService;
import org.rosuda.util.process.RUNSTATE;

public class DBStarterTest {

	//since this relies on a startDBScript (of some kind) maybe the whole idea needs second thoughts ?
	//the startCommand might be set inside the spring source alternatively

	@Test
	public void testRun() {
		final ProcessService<Connection> service = new DatabaseStarterFactory().createService();
		Assert.assertNotNull(service);
		service.start();
		Assert.assertEquals(RUNSTATE.RUNNING, service.getRunState());
	}
}
