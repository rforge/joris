package org.rosuda.integration.connection.jrengine;

import static org.junit.Assert.assertNotNull;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.rosuda.integration.suites.util.PlainJavaConnectionTestSuiteContext;
import org.rosuda.irconnect.IRConnection;
import org.rosuda.irconnect.IREXP;
import org.rosuda.irconnect.output.ObjectFormatter;

public class WrappedEngineFormatIntegrationTest {

    private ObjectFormatter objectFormatter;

    @Before
    public void setUp() throws Exception {
        final Properties config = new Properties();
        config.load(WrappedEngineIntegrationTest.class.getResourceAsStream("/org/rosuda/irconnect/config.properties"));
        final String configurationProperties = "/org/rosuda/irconnect/" + config.getProperty("mode") + ".properties";
        final Properties testConfiguration = new Properties();
        testConfiguration.load(WrappedEngineIntegrationTest.class.getResourceAsStream(configurationProperties));
        objectFormatter = new ObjectFormatter();
    }

    @Test
    public void testLinearModel() {
        final IRConnection connection = PlainJavaConnectionTestSuiteContext.getInstance().acquireRConnection();
        connection.eval("data(cars)");
        connection.eval("attach(cars)");

        final IREXP linearModel = connection.eval("lm(dist~speed)");
        final String formattedLM = objectFormatter.format(linearModel);
        assertNotNull(formattedLM);
    }
}
