package org.rosuda.integration.connection.mapper;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rosuda.integration.suites.util.PlainJavaConnectionTestSuiteContext;
import org.rosuda.irconnect.IREXP;
import org.rosuda.mapper.ObjectTransformationHandler;
import org.rosuda.mapper.irexp.IREXPMapper;
import org.rosuda.type.Node;
import org.rosuda.type.impl.NodeBuilderFactory;

/**
 * requires running RServe instance
 * 
 * @author ralfseger
 * 
 */
public class RTypeConversionIntegrationTest{ 

    private ObjectTransformationHandler<Object> handler;

    @Before
    public void setUp() throws Exception {
        this.handler = new IREXPMapper<Object>().createInstance();
    }

    @Test
    public void testStringConversion() throws ParserConfigurationException, TransformerException {
        final IREXP stringREXP = PlainJavaConnectionTestSuiteContext.getInstance().acquireRConnection().eval("\"aString\"");
        Assert.assertEquals(IREXP.XT_STR, stringREXP.getType());
        final Node.Builder<Object> stringNode = new NodeBuilderFactory<Object>().createRoot();
        handler.transform(stringREXP, stringNode);
        Assert.assertNotNull(stringNode);
    }

    @Test
    public void testDoubleConversion() throws ParserConfigurationException, TransformerException {
        final IREXP doubleREXP = PlainJavaConnectionTestSuiteContext.getInstance().acquireRConnection().eval("1/7");
        Assert.assertEquals(IREXP.XT_DOUBLE, doubleREXP.getType());
        final Node.Builder<Object> doubleNode = new NodeBuilderFactory<Object>().createRoot();
        handler.transform(doubleREXP, doubleNode);
        Assert.assertNotNull(doubleNode);
    }

    @Test
    public void testIntegerConversion() throws ParserConfigurationException, TransformerException {
        final IREXP intREXP = PlainJavaConnectionTestSuiteContext.getInstance().acquireRConnection().eval("as.integer(1+7)");
        Assert.assertEquals(IREXP.XT_INT, intREXP.getType());
        final Node.Builder<Object> intNode = new NodeBuilderFactory<Object>().createRoot();
        handler.transform(intREXP, intNode);
        Assert.assertNotNull(intNode);
    }

    @Test
    public void testBooleanConversion() throws ParserConfigurationException, TransformerException {
        final IREXP boolREXP = PlainJavaConnectionTestSuiteContext.getInstance().acquireRConnection().eval("1 == 1");
        Assert.assertEquals(IREXP.XT_BOOL, boolREXP.getType());
        final Node.Builder<Object> boolNode = new NodeBuilderFactory<Object>().createRoot();
        handler.transform(boolREXP, boolNode);
        Assert.assertNotNull(boolNode);
    }

    @Test
    public void testFactorConversion() throws ParserConfigurationException, TransformerException {
        final IREXP factorREXP = PlainJavaConnectionTestSuiteContext.getInstance().acquireRConnection().eval("as.factor(x=c(\"a\",\"b\"))");
        Assert.assertEquals(IREXP.XT_FACTOR, factorREXP.getType());
        final Node.Builder<Object> factorNode = new NodeBuilderFactory<Object>().createRoot();
        handler.transform(factorREXP, factorNode);
        Assert.assertNotNull(factorNode);
    }

    @Test
    public void testLM() throws ParserConfigurationException, TransformerException {
        final IREXP lmREXP = PlainJavaConnectionTestSuiteContext.getInstance().acquireRConnection().eval("lm(speed~dist,data=cars)");
        Assert.assertEquals(IREXP.XT_MAP, lmREXP.getType());
        final Node.Builder<Object> lmNode = new NodeBuilderFactory<Object>().createRoot();
        handler.transform(lmREXP, lmNode);
        Assert.assertNotNull(lmNode);
    }

    @Test
    public void testLMSummary() throws ParserConfigurationException, TransformerException {
        final IREXP lmREXP = PlainJavaConnectionTestSuiteContext.getInstance().acquireRConnection().eval("summary(lm(speed~dist,data=cars))");
        Assert.assertEquals(IREXP.XT_MAP, lmREXP.getType());
        final Node.Builder<Object> lmNode = new NodeBuilderFactory<Object>().createRoot();
        handler.transform(lmREXP, lmNode);
        Assert.assertNotNull(lmNode);
    }

}
