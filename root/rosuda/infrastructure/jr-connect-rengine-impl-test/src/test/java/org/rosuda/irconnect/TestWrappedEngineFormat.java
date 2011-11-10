package org.rosuda.irconnect;

import static org.junit.Assert.assertNotNull;

import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rosuda.irconnect.output.ObjectFormatter;
import org.rosuda.rengine.REngineConnectionFactory;

public class TestWrappedEngineFormat extends AbstractRTestCase {

	IRConnection connection;
	ObjectFormatter objectFormatter;

    @Before
	public void setUp() throws Exception {
		final Properties config = new Properties();
        config.load(TestWrappedEngine.class.getResourceAsStream("/org/rosuda/irconnect/config.properties"));
        final String configurationProperties = "/org/rosuda/irconnect/"+config.getProperty("mode")+".properties";
		final Properties testConfiguration = new Properties();
        testConfiguration.load(TestWrappedEngine.class.getResourceAsStream(configurationProperties));
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
