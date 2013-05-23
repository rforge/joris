package org.rosuda.integration.connection.jrengine;

/**
 * @author ralfseger 
 * ensure that Rserve is running and use the correct rserve
 * e.g:
 * C:\Program Files\R\R-2.12.1\bin\x64>R CMD Rserve_x64 start
 * Rserve: Ok, ready to answer queries.
 */
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.RList;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

//tests Rserve.RConnection
@Ignore
public class EngineIntegrationTest {

    RConnection connection;

    @Before
    public void setUp() throws Exception {
        connection = new RConnection();
    }

    @After
    public void tearDown() throws Exception {
        connection.close();
    }

    @Test
    public void testConnection() {
        Assert.assertNotNull("not connected", connection);
    }

    @Test
    public void testLibraries() throws RserveException, REXPMismatchException {
        final REXP libraryREXP = connection.eval(new StringBuffer().append("try(").append("library()").append(")").toString());

        final RList namedRList = libraryREXP.asList();
        final String[] keys = namedRList.keys();
        Assert.assertEquals("header", keys[0]);
        Assert.assertEquals("results", keys[1]);
        Assert.assertEquals("footer", keys[2]);
        final REXP header = namedRList.at("header");
        Assert.assertTrue(header.isNull());
        final REXP footer = namedRList.at("footer");
        Assert.assertTrue(footer.isNull());
        final REXP results = namedRList.at("results");
        Assert.assertNotNull(results.dim());
        Assert.assertEquals(3, results.dim()[1]);
        Assert.assertTrue(results.isString());
        Assert.assertTrue(results.length() > 1);
        final String[] strings = results.asStrings();
        Assert.assertTrue(results.hasAttribute("dim"));
        final REXP dim = results.getAttribute("dim");
        Assert.assertTrue(dim.isInteger());
        Assert.assertTrue(dim.length() == 2);
        final int[] dimensions = dim.asIntegers();
        Assert.assertEquals(3, dimensions[1]);
        final int rows = dimensions[0];
        Assert.assertEquals(rows * 3, results.length());

        final int length = dimensions[0];
        final List<WrappedEngineIntegrationTest.Library> libs = new ArrayList<WrappedEngineIntegrationTest.Library>();
        for (int i = 0; i < length; i++) {
            final WrappedEngineIntegrationTest.Library lib = new WrappedEngineIntegrationTest.Library();
            lib.name = strings[i];
            lib.active = false;// loadedLibraries.contains(lib.name);
            lib.lib = strings[i + length];
            lib.desc = strings[i + 2 * length];
            libs.add(lib);
        }
    }
}
