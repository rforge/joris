package org.rosuda.util.r.inttest;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.rosuda.linux.socket.NativeSocketLibUtil;
import org.slf4j.LoggerFactory;
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
@ContextConfiguration(locations = { "classpath:/spring/r-socket-service.spring.xml" })
@Configurable
public class RSocketStarterIntegrationTest extends AbstractStarterIntegrationTest {

    // seem
    @BeforeClass
    public static void initNativeLibsForSpring() {
        try {
            new NativeSocketLibUtil().resetCache();
        } catch (final Throwable t) {
            LoggerFactory.getLogger(RSocketStarterIntegrationTest.class).error("could not init NativeSocketLibs before Class", t);
        }
    }
}
