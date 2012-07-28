package org.rosuda.irconnect.inttest;

import static org.junit.Assert.assertNotNull;

import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rosuda.irconnect.IRConnection;
import org.rosuda.irconnect.IREXP;
import org.rosuda.irconnect.output.ObjectFormatter;
import org.rosuda.rengine.REngineConnectionFactory;

public class WrappedEngineFormatIntegrationTest extends AbstractRIntegrationTest {

	private IRConnection connection;
	private ObjectFormatter objectFormatter;

    @Before
	public void setUp() throws Exception {
		final Properties config = new Properties();
        config.load(WrappedEngineIntegrationTest.class.getResourceAsStream("/org/rosuda/irconnect/config.properties"));
        final String configurationProperties = "/org/rosuda/irconnect/"+config.getProperty("mode")+".properties";
		final Properties testConfiguration = new Properties();
        testConfiguration.load(WrappedEngineIntegrationTest.class.getResourceAsStream(configurationProperties));
        connection = new REngineConnectionFactory().createRConnection(testConfiguration);
        objectFormatter = new ObjectFormatter();
    }

    @After
    public void tearDown() throws Exception {
		connection.close();
	}

	@Test
	public void testLinearModel() {
		connection.eval("data(cars)");
		connection.eval("attach(cars)");
		
		final IREXP linearModel = connection.eval("lm(dist~speed)");
		final String formattedLM = objectFormatter.format(linearModel);
		assertNotNull(formattedLM);
	}
}
