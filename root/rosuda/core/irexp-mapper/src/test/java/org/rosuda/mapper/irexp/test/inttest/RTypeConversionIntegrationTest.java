package org.rosuda.mapper.irexp.test.inttest;

import java.util.Properties;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rosuda.irconnect.IREXP;
import org.rosuda.irconnect.ITwoWayConnection;
import org.rosuda.mapper.ObjectTransformationHandler;
import org.rosuda.mapper.irexp.IREXPMapper;
import org.rosuda.rengine.REngineConnectionFactory;
import org.rosuda.type.Node;
import org.rosuda.type.impl.NodeBuilderFactory;

/**
 * requires running RServe instance
 * @author ralfseger
 *
 */
public class RTypeConversionIntegrationTest extends AbstractRIntegrationTest {

	private static final Logger logger = Logger.getLogger(RTypeConversionIntegrationTest.class.getCanonicalName());
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
	public void testStringConversion() throws ParserConfigurationException, TransformerException {
		if (connection == null) {
			logger.severe("Rserve is not running, test cannot work.");
			return;
		}
		final IREXP stringREXP = connection.eval("\"aString\"");
		Assert.assertEquals(IREXP.XT_STR, stringREXP.getType());
		final Node.Builder<Object> stringNode = new NodeBuilderFactory<Object>().createRoot();
		handler.transform(stringREXP, stringNode);
		Assert.assertNotNull(stringNode);
		System.out.println(stringNode);
	}
	
	@Test
	public void testDoubleConversion() throws ParserConfigurationException, TransformerException {
		if (connection == null) { 			logger.severe("Rserve is not running, test cannot work."); 			return; 		}
		final IREXP doubleREXP = connection.eval("1/7");
		Assert.assertEquals(IREXP.XT_DOUBLE, doubleREXP.getType());
		final Node.Builder<Object> doubleNode = new NodeBuilderFactory<Object>().createRoot();
		handler.transform(doubleREXP, doubleNode);
		Assert.assertNotNull(doubleNode);
		System.out.println(doubleNode);
	}
	
	@Test
	public void testIntegerConversion() throws ParserConfigurationException, TransformerException {
		if (connection == null) { 			logger.severe("Rserve is not running, test cannot work."); 			return; 		}
		final IREXP intREXP = connection.eval("as.integer(1+7)");
		Assert.assertEquals(IREXP.XT_INT, intREXP.getType());
		final Node.Builder<Object> intNode = new NodeBuilderFactory<Object>().createRoot();
		handler.transform(intREXP, intNode);
		Assert.assertNotNull(intNode);
		System.out.println(intNode);
	}
	
	@Test
	public void testBooleanConversion() throws ParserConfigurationException, TransformerException {
		if (connection == null) { 			logger.severe("Rserve is not running, test cannot work."); 			return; 		}
		final IREXP boolREXP = connection.eval("1 == 1");
		Assert.assertEquals(IREXP.XT_BOOL, boolREXP.getType());
		final Node.Builder<Object> boolNode = new NodeBuilderFactory<Object>().createRoot();
		handler.transform(boolREXP, boolNode);
		Assert.assertNotNull(boolNode);
		System.out.println(boolNode);
	}
	
	@Test
	public void testFactorConversion() throws ParserConfigurationException, TransformerException {
		if (connection == null) { 			logger.severe("Rserve is not running, test cannot work."); 			return; 		}
		final IREXP factorREXP = connection.eval("as.factor(x=c(\"a\",\"b\"))");
		Assert.assertEquals(IREXP.XT_FACTOR, factorREXP.getType());
		final Node.Builder<Object> factorNode = new NodeBuilderFactory<Object>().createRoot();
		handler.transform(factorREXP, factorNode);
		Assert.assertNotNull(factorNode);
		System.out.println(factorNode);
	}
	
	@Test
	public void testLM() throws ParserConfigurationException, TransformerException {
		if (connection == null) { 			logger.severe("Rserve is not running, test cannot work."); 			return; 		}
		final IREXP lmREXP = connection.eval("lm(speed~dist,data=cars)");
		Assert.assertEquals(IREXP.XT_MAP, lmREXP.getType());
		final long tick = System.currentTimeMillis();
		final Node.Builder<Object> lmNode = new NodeBuilderFactory<Object>().createRoot();
		handler.transform(lmREXP, lmNode);
		System.out.println("performance="+(System.currentTimeMillis()-tick)+" ms.");
		Assert.assertNotNull(lmNode);
		System.out.println(lmNode);	
	}
	
	@Test
	public void testLMSummary() throws ParserConfigurationException, TransformerException {
		if (connection == null) {
			logger.severe("Rserve is not running, test cannot work.");
			return;
		}
		final IREXP lmREXP = connection.eval("summary(lm(speed~dist,data=cars))");
		Assert.assertEquals(IREXP.XT_MAP, lmREXP.getType());
		final long tick = System.currentTimeMillis();
		final Node.Builder<Object> lmNode = new NodeBuilderFactory<Object>().createRoot();
		handler.transform(lmREXP, lmNode);
		System.out.println("performance="+(System.currentTimeMillis()-tick)+" ms.");
		Assert.assertNotNull(lmNode);
		System.out.println(lmNode);	
	}
	
}
