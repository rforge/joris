package org.rosuda.mapper.irexp.test;

import java.util.Properties;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import junit.framework.TestCase;

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
public class RTypeConversionTest extends TestCase {

	private static final Logger logger = Logger.getLogger(RTypeConversionTest.class.getCanonicalName());
	private ITwoWayConnection connection;
	private ObjectTransformationHandler<Object> handler;
	@Override
	protected void setUp() throws Exception {
		super.setUp();
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
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		connection.close();
	}
	
	public void testStringConversion() throws ParserConfigurationException, TransformerException {
		if (connection == null) {
			logger.severe("Rserve is not running, test cannot work.");
			return;
		}
		final IREXP stringREXP = connection.eval("\"aString\"");
		assertEquals(IREXP.XT_STR, stringREXP.getType());
		final Node.Builder<Object> stringNode = new NodeBuilderFactory<Object>().createRoot();
		handler.transform(stringREXP, stringNode);
		assertNotNull(stringNode);
		System.out.println(stringNode);
	}
	
	public void testDoubleConversion() throws ParserConfigurationException, TransformerException {
		if (connection == null) { 			logger.severe("Rserve is not running, test cannot work."); 			return; 		}
		final IREXP doubleREXP = connection.eval("1/7");
		assertEquals(IREXP.XT_DOUBLE, doubleREXP.getType());
		final Node.Builder<Object> doubleNode = new NodeBuilderFactory<Object>().createRoot();
		handler.transform(doubleREXP, doubleNode);
		assertNotNull(doubleNode);
		System.out.println(doubleNode);
	}
	
	public void testIntegerConversion() throws ParserConfigurationException, TransformerException {
		if (connection == null) { 			logger.severe("Rserve is not running, test cannot work."); 			return; 		}
		final IREXP intREXP = connection.eval("as.integer(1+7)");
		assertEquals(IREXP.XT_INT, intREXP.getType());
		final Node.Builder<Object> intNode = new NodeBuilderFactory<Object>().createRoot();
		handler.transform(intREXP, intNode);
		assertNotNull(intNode);
		System.out.println(intNode);
	}
	
	public void testBooleanConversion() throws ParserConfigurationException, TransformerException {
		if (connection == null) { 			logger.severe("Rserve is not running, test cannot work."); 			return; 		}
		final IREXP boolREXP = connection.eval("1 == 1");
		assertEquals(IREXP.XT_BOOL, boolREXP.getType());
		final Node.Builder<Object> boolNode = new NodeBuilderFactory<Object>().createRoot();
		handler.transform(boolREXP, boolNode);
		assertNotNull(boolNode);
		System.out.println(boolNode);
	}
	
	public void testFactorConversion() throws ParserConfigurationException, TransformerException {
		if (connection == null) { 			logger.severe("Rserve is not running, test cannot work."); 			return; 		}
		final IREXP factorREXP = connection.eval("as.factor(x=c(\"a\",\"b\"))");
		assertEquals(IREXP.XT_FACTOR, factorREXP.getType());
		final Node.Builder<Object> factorNode = new NodeBuilderFactory<Object>().createRoot();
		handler.transform(factorREXP, factorNode);
		assertNotNull(factorNode);
		System.out.println(factorNode);
	}
	
	public void testLM() throws ParserConfigurationException, TransformerException {
		if (connection == null) { 			logger.severe("Rserve is not running, test cannot work."); 			return; 		}
		final IREXP lmREXP = connection.eval("lm(speed~dist,data=cars)");
		assertEquals(IREXP.XT_MAP, lmREXP.getType());
		final long tick = System.currentTimeMillis();
		final Node.Builder<Object> lmNode = new NodeBuilderFactory<Object>().createRoot();
		handler.transform(lmREXP, lmNode);
		System.out.println("performance="+(System.currentTimeMillis()-tick)+" ms.");
		assertNotNull(lmNode);
		System.out.println(lmNode);	
	}
	
	public void testLMSummary() throws ParserConfigurationException, TransformerException {
		if (connection == null) {
			logger.severe("Rserve is not running, test cannot work.");
			return;
		}
		final IREXP lmREXP = connection.eval("summary(lm(speed~dist,data=cars))");
		assertEquals(IREXP.XT_MAP, lmREXP.getType());
		final long tick = System.currentTimeMillis();
		final Node.Builder<Object> lmNode = new NodeBuilderFactory<Object>().createRoot();
		handler.transform(lmREXP, lmNode);
		System.out.println("performance="+(System.currentTimeMillis()-tick)+" ms.");
		assertNotNull(lmNode);
		System.out.println(lmNode);	
	}
	
}
