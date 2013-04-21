package org.rosuda.util.r.inttest;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * this tests the start/stop of an REngine-Server-Process
 * 
 * @author ralfseger
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/spring/r-socket-service.spring.xml" })
@Configurable
public class RSocketStarterIntegrationTest extends AbstractStarterIntegrationTest {

}
