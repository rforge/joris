package org.rosuda.util.db.inttest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.ServerSocket;

import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/spring/derby-service.spring.xml"})
@Configurable
public class DatabaseSetupTest {

	@Autowired
	private DataSource dataSource;
	
	@Test(timeout=30000)
	public void testUserDatasourceAvailable() {
		assertNotNull("no user data source could be created", dataSource);
		try {
			final ServerSocket socket = new ServerSocket(1600);
			assertFalse(socket.isClosed());
			assertTrue(socket.isBound());
		} catch (IOException e) {
			fail("db port is not open");
		}
	}
}
