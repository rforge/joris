package org.rosuda.mapper.irexp.test.inttest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Properties;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rosuda.irconnect.IREXP;
import org.rosuda.irconnect.ITwoWayConnection;
import org.rosuda.mapper.ObjectTransformationHandler;
import org.rosuda.mapper.irexp.IREXPMapper;
import org.rosuda.rengine.REngineConnectionFactory;
import org.rosuda.type.Node;
import org.rosuda.type.impl.NodeBuilderFactory;

public class RModelSerializationIntegrationTest extends AbstractRIntegrationTest {

	private static final Logger logger = Logger.getLogger(RModelSerializationIntegrationTest.class.getCanonicalName());
	private ITwoWayConnection connection;
	private ObjectTransformationHandler<Object> handler;
	
	@Before
	public void setUp() throws Exception {
		if (connection != null)
			return; //reuse old con
		this.handler = new IREXPMapper<Object>().createInstance();

		final Properties properties = new Properties();
		try {
			connection = REngineConnectionFactory.getInstance().createTwoWayConnection(properties);
		} catch (final Exception x) {
			x.printStackTrace();
		}
	}
	
	@After
	public void tearDown() throws Exception {
		connection.close();
	}
	
	@Test
	public void testWriteLMSummary() throws ParserConfigurationException, TransformerException, IOException {
		if (connection == null) {
			logger.severe("Rserve is not running, test cannot work.");
			return;
		}
		final IREXP lmREXP = connection.eval("c(summary(lm(speed~dist,data=cars),AIC=AIC(lm(speed~dist,data=cars))))");
		Assert.assertEquals(IREXP.XT_MAP, lmREXP.getType());
		final Node.Builder<Object> lmNode = new NodeBuilderFactory<Object>().createRoot();
		handler.transform(lmREXP, lmNode);
		Assert.assertNotNull(lmNode);
		final File file = File.createTempFile("lmsummary", "rObj");
		final ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
		oos.writeObject(lmNode.build());
		oos.close();
		Assert.assertNotNull(oos);
		Assert.assertEquals(8070,file.length());
		//file.deleteOnExit();
	}
	
	@Test
	public void testReadLMSummary() throws ParserConfigurationException, TransformerException, IOException, ClassNotFoundException {
		final ObjectInputStream ois = new ObjectInputStream(RModelSerializationIntegrationTest.class.getResourceAsStream("/models/extendedLmSummary.rObj"));
		final Object rootNode = ois.readObject();
		ois.close();
		Assert.assertNotNull(ois);
		Assert.assertTrue(Node.class.isAssignableFrom(rootNode.getClass()));
	}
}
