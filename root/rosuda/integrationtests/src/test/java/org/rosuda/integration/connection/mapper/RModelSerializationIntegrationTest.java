package org.rosuda.integration.connection.mapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.rosuda.integration.suites.util.PlainJavaConnectionTestSuiteContext;
import org.rosuda.irconnect.IREXP;
import org.rosuda.mapper.ObjectTransformationHandler;
import org.rosuda.mapper.irexp.IREXPMapper;
import org.rosuda.type.Node;
import org.rosuda.type.impl.NodeBuilderFactory;

public class RModelSerializationIntegrationTest {

    private ObjectTransformationHandler<Object> handler;

    @Before
    public void setUp() throws Exception {
        this.handler = new IREXPMapper<Object>().createInstance();
    }

    @Test
    public void testWriteLMSummary() throws ParserConfigurationException, TransformerException, IOException {
        final IREXP lmREXP = PlainJavaConnectionTestSuiteContext.getInstance().acquireRConnection().eval("c(summary(lm(speed~dist,data=cars),AIC=AIC(lm(speed~dist,data=cars))))");
        Assert.assertEquals(IREXP.XT_MAP, lmREXP.getType());
        final Node.Builder<Object> lmNode = new NodeBuilderFactory<Object>().createRoot();
        handler.transform(lmREXP, lmNode);
        Assert.assertNotNull(lmNode);
        final File file = File.createTempFile("lmsummary", "rObj");
        final ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
        oos.writeObject(lmNode.build());
        oos.close();
        Assert.assertNotNull(oos);
        Assert.assertEquals(8070, file.length());
        // file.deleteOnExit();
    }

    @Test
    public void testReadLMSummary() throws ParserConfigurationException, TransformerException, IOException, ClassNotFoundException {
        final ObjectInputStream ois = new ObjectInputStream(
                RModelSerializationIntegrationTest.class.getResourceAsStream("/models/extendedLmSummary.rObj"));
        final Object rootNode = ois.readObject();
        ois.close();
        Assert.assertNotNull(ois);
        Assert.assertTrue(Node.class.isAssignableFrom(rootNode.getClass()));
    }
}
