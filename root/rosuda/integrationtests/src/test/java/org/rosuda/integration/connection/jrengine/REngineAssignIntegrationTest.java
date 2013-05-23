/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rosuda.integration.connection.jrengine;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rosuda.integration.suites.util.PlainJavaConnectionTestSuiteContext;
import org.rosuda.irconnect.IRBool;
import org.rosuda.irconnect.IRConnection;
import org.rosuda.irconnect.IREXP;
import org.rosuda.irconnect.IREXPConstants;
import org.rosuda.irconnect.IRFactor;
import org.rosuda.irconnect.IRMap;
import org.rosuda.irconnect.IRMatrix;
import org.rosuda.irconnect.IRVector;
import org.rosuda.irconnect.ITwoWayConnection;

/**
 * 
 * @author Ralf
 */
public class REngineAssignIntegrationTest {

    @Before
    public void setUp() throws Exception {
        final Properties config = new Properties();
        config.load(WrappedEngineIntegrationTest.class.getResourceAsStream("/org/rosuda/irconnect/config.properties"));
        final String configurationProperties = "/org/rosuda/irconnect/" + config.getProperty("mode") + ".properties";
        final Properties testConfiguration = new Properties();
        testConfiguration.load(WrappedEngineIntegrationTest.class.getResourceAsStream(configurationProperties));
    }

    @Test
    public void testConnection() {
        final IRConnection connection = PlainJavaConnectionTestSuiteContext.getInstance().acquireRConnection();
        Assert.assertNotNull("not connected", connection);
        Assert.assertTrue("no proxy !", Proxy.isProxyClass(connection.getClass()));
    }

    @Test
    public void testList() {
        final IREXP libraryREXP = PlainJavaConnectionTestSuiteContext.getInstance().acquireRConnection().eval(new StringBuffer().append("try(").append("library()").append(")").toString());

        Assert.assertEquals(IREXP.XT_MAP, libraryREXP.getType());
        final IRMap namedRList = libraryREXP.asMap();
        final String[] keys = namedRList.keys();
        Assert.assertEquals("header", keys[0]);
        Assert.assertEquals("results", keys[1]);
        Assert.assertEquals("footer", keys[2]);
        final IREXP header = namedRList.at("header");
        Assert.assertTrue(header.getType() == IREXP.XT_NULL);
        final IREXP footer = namedRList.at("footer");
        Assert.assertTrue(footer.getType() == IREXP.XT_NULL);
        final IREXP results = namedRList.at("results");
        Assert.assertNotNull(results.dim());
        Assert.assertEquals(3, results.dim()[1]);
        Assert.assertTrue(results.getType() == IREXP.XT_ARRAY_STR);
        Assert.assertTrue(results.length() > 1);
        final String[] strings = results.asStringArray();
        final int rows = results.dim()[0];
        Assert.assertEquals(rows * 3, results.length());

        final List<Library> libs = new ArrayList<Library>();
        for (int i = 0; i < rows; i++) {
            final Library lib = new Library();
            lib.name = strings[i];
            lib.active = false;// loadedLibraries.contains(lib.name);
            lib.lib = strings[i + rows];
            lib.desc = strings[i + 2 * rows];
            libs.add(lib);
        }
    }

    @Test
    public void testString() {
        final IREXP libraryREXP = PlainJavaConnectionTestSuiteContext.getInstance().acquireRConnection().eval(new StringBuffer().append("try(").append("\"string\"").append(")").toString());
        Assert.assertNotNull(libraryREXP);
        Assert.assertEquals(IREXP.XT_STR, libraryREXP.getType());
        Assert.assertEquals("string", libraryREXP.asString());
    }

    @Test
    public void testDouble() {
        final IREXP libraryREXP = PlainJavaConnectionTestSuiteContext.getInstance().acquireRConnection().eval(new StringBuffer().append("try(").append("1/4").append(")").toString());
        Assert.assertNotNull(libraryREXP);
        Assert.assertEquals(IREXP.XT_DOUBLE, libraryREXP.getType());
        Assert.assertEquals(0.25, libraryREXP.asDouble(), PlainJavaConnectionTestSuiteContext.EPSILON);
    }

    @Test
    public void testInt() {
        final IREXP libraryREXP = PlainJavaConnectionTestSuiteContext.getInstance().acquireRConnection().eval(new StringBuffer().append("try(").append("as.integer(1+4)").append(")").toString());
        Assert.assertNotNull(libraryREXP);
        Assert.assertEquals(IREXP.XT_INT, libraryREXP.getType());
        Assert.assertEquals(5, libraryREXP.asInt());
    }

    @Test
    public void testBool() {
        final IREXP libraryREXP = PlainJavaConnectionTestSuiteContext.getInstance().acquireRConnection().eval(new StringBuffer().append("try(").append("1 == 1").append(")").toString());
        Assert.assertNotNull(libraryREXP);
        Assert.assertEquals(IREXP.XT_BOOL, libraryREXP.getType());
        Assert.assertEquals(true, libraryREXP.asBool().isTRUE());
    }

    @Test
    public void testSymbol() {
        final IREXP libraryREXP = PlainJavaConnectionTestSuiteContext.getInstance().acquireRConnection().eval(new StringBuffer().append("try(").append("as.symbol(\"x\")").append(")").toString());
        Assert.assertNotNull(libraryREXP);
        Assert.assertEquals(IREXP.XT_SYM, libraryREXP.getType());
        Assert.assertEquals("x", libraryREXP.asSymbol().asString());
    }

    @Test
    public void testFactor() {
        final IREXP libraryREXP = PlainJavaConnectionTestSuiteContext.getInstance().acquireRConnection().eval(new StringBuffer().append("try(").append("as.factor(c(1,1,1,2,2,1,3,2,3,1))").append(")")
                .toString());
        Assert.assertNotNull(libraryREXP);
        Assert.assertEquals(IREXP.XT_FACTOR, libraryREXP.getType());
        final IRFactor factor = libraryREXP.asFactor();
        Assert.assertNotNull(factor);
        Assert.assertEquals(10, factor.size());
        Assert.assertEquals("1", factor.at(0));
        Assert.assertEquals("1", factor.at(1));
        Assert.assertEquals("1", factor.at(2));
        Assert.assertEquals("2", factor.at(3));
        Assert.assertEquals("2", factor.at(4));
        Assert.assertEquals("1", factor.at(5));
        Assert.assertEquals("3", factor.at(6));
        Assert.assertEquals("2", factor.at(7));
        Assert.assertEquals("3", factor.at(8));
        Assert.assertEquals("1", factor.at(9));
    }

    @Test
    public void testStringArray() {
        final IREXP libraryREXP = PlainJavaConnectionTestSuiteContext.getInstance().acquireRConnection().eval(new StringBuffer().append("try(").append("c(\"string\",\"string2\")").append(")")
                .toString());
        Assert.assertNotNull(libraryREXP);
        Assert.assertEquals(IREXP.XT_ARRAY_STR, libraryREXP.getType());
        final String[] sArray = libraryREXP.asStringArray();
        Assert.assertNotNull(sArray);
        Assert.assertEquals("string", sArray[0]);
        Assert.assertEquals("string2", sArray[1]);
    }

    @Test
    public void testDoubleArray() {
        final IREXP libraryREXP = PlainJavaConnectionTestSuiteContext.getInstance().acquireRConnection().eval(new StringBuffer().append("try(").append("c(1/4,1/7)").append(")").toString());
        Assert.assertNotNull(libraryREXP);
        Assert.assertEquals(IREXP.XT_ARRAY_DOUBLE, libraryREXP.getType());
        final double[] dArray = libraryREXP.asDoubleArray();
        Assert.assertEquals(0.25, dArray[0], PlainJavaConnectionTestSuiteContext.EPSILON);
        Assert.assertEquals(1.0 / 7.0, dArray[1], PlainJavaConnectionTestSuiteContext.EPSILON);
    }

    @Test
    public void testIntArray() {
        final IREXP libraryREXP = PlainJavaConnectionTestSuiteContext.getInstance().acquireRConnection().eval(new StringBuffer().append("try(").append("c(as.integer(1+4),as.integer(1+5))")
                .append(")").toString());
        Assert.assertNotNull(libraryREXP);
        Assert.assertEquals(IREXP.XT_ARRAY_INT, libraryREXP.getType());
        final int[] iArray = libraryREXP.asIntArray();
        Assert.assertEquals(5, iArray[0]);
        Assert.assertEquals(6, iArray[1]);
    }

    @Test
    public void testBoolArray() {
        final IREXP libraryREXP = PlainJavaConnectionTestSuiteContext.getInstance().acquireRConnection().eval(new StringBuffer().append("try(").append("c(1 == 1, 1 ==2)").append(")").toString());
        Assert.assertNotNull(libraryREXP);
        Assert.assertEquals(IREXP.XT_ARRAY_BOOL, libraryREXP.getType());
        IRBool[] bArray = libraryREXP.asBoolArray();
        Assert.assertEquals(true, bArray[0].isTRUE());
        Assert.assertEquals(true, bArray[1].isFALSE());
    }

    @Test
    public void testVector() {
        final IREXP testREXP = PlainJavaConnectionTestSuiteContext.getInstance().acquireRConnection().eval("c(a = as.integer(1), a2 = 2.12, b = 2 == 1, c = as.symbol(\"s\"))");
        Assert.assertNotNull(testREXP);
        // TODO distinguish between MAP and VECTOR
        Assert.assertEquals(IREXP.XT_MAP, testREXP.getType());
        final IRVector testVector = testREXP.asVector();
        Assert.assertNotNull(testVector);
        Assert.assertEquals(4, testVector.size());
        final IREXP at0 = testVector.at(0);
        Assert.assertNotNull(at0);
        Assert.assertEquals(at0.getType(), IREXP.XT_INT);

        final IREXP at1 = testVector.at(1);
        Assert.assertNotNull(at1);
        Assert.assertEquals(at1.getType(), IREXP.XT_DOUBLE);
    }

    @Test
    public void testLibraries() {
        final IREXP testREXP = PlainJavaConnectionTestSuiteContext.getInstance().acquireRConnection().eval(new StringBuffer().append("try(").append("library()").append(")").toString());
        Assert.assertEquals("testREXP is not a List", IREXP.XT_MAP, testREXP.getType());
        final IRMap elementMap = testREXP.asMap();
        // debug names
        Assert.assertEquals("elementList.header is not XT_NULL", IREXP.XT_NULL, elementMap.at("header").getType());
        Assert.assertEquals("elementList.footer is not a XT_NULL", IREXP.XT_NULL, elementMap.at("footer").getType());
        Assert.assertEquals("elementList.keys #0 is not header", "header", elementMap.keys()[0]);
        Assert.assertEquals("elementList.keys #1 is not results", "results", elementMap.keys()[1]);
        Assert.assertEquals("elementList.keys #2 is not footer", "footer", elementMap.keys()[2]);

        Assert.assertEquals("elementList.getBody is not XT_ARRAY_STR", IREXP.XT_ARRAY_STR, elementMap.at("results").getType());
        final String[] listBody = elementMap.at("results").asStringArray();
        // TODO long list
        Assert.assertTrue("no of elements is not divisible by 3", listBody.length % 3 == 0);
    }

    @Test
    public void testMatrix() {
        final IREXP testREXP = PlainJavaConnectionTestSuiteContext.getInstance().acquireRConnection().eval(new StringBuffer().append("try(").append("summary(lm(dist~speed,data=cars))$coefficients")
                .append(")").toString());
        Assert.assertEquals("textREXP is not a matrix", IREXP.XT_MATRIX, testREXP.getType());
        final IRMatrix matrix = testREXP.asMatrix();
        Assert.assertNotNull("conversion asMatrx failed!", matrix);
        Assert.assertEquals("not 2 rows", 2, matrix.getRows());
        Assert.assertEquals("not 4 columns", 4, matrix.getColumns());

        Assert.assertEquals("row #1 name wrong", "(Intercept)", matrix.getRowNameAt(0));
        Assert.assertEquals("row #2 name wrong", "speed", matrix.getRowNameAt(1));

        Assert.assertEquals("column #1 name wrong", "Estimate", matrix.getColumnNameAt(0));
        Assert.assertEquals("column #2 name wrong", "Std. Error", matrix.getColumnNameAt(1));
        Assert.assertEquals("column #3 name wrong", "t value", matrix.getColumnNameAt(2));
        Assert.assertEquals("column #4 name wrong", "Pr(>|t|)", matrix.getColumnNameAt(3));

        // just once the values
        for (int row = 0; row < matrix.getRows(); row++) {
            for (int col = 0; col < matrix.getColumns(); col++) {
                Assert.assertEquals("coeff type is wrong @(row=" + row + ",col=" + col + ")", matrix.getValueAt(row, col).getType(),
                        IREXP.XT_DOUBLE);
            }
        }
        Assert.assertEquals("coeff value is wrong @(row=0,col=0)", -17.579095, matrix.getValueAt(0, 0).asDouble(),
                PlainJavaConnectionTestSuiteContext.EPSILON);
        Assert.assertEquals("coeff value is wrong @(row=0,col=1)", 6.7584402, matrix.getValueAt(0, 1).asDouble(),
                PlainJavaConnectionTestSuiteContext.EPSILON);
        Assert.assertEquals("coeff value is wrong @(row=0,col=2)", -2.601058, matrix.getValueAt(0, 2).asDouble(),
                PlainJavaConnectionTestSuiteContext.EPSILON);
        Assert.assertEquals("coeff value is wrong @(row=0,col=3)", 1.231882e-02, matrix.getValueAt(0, 3).asDouble(),
                PlainJavaConnectionTestSuiteContext.EPSILON);
        Assert.assertEquals("coeff value is wrong @(row=1,col=0)", 3.932409, matrix.getValueAt(1, 0).asDouble(),
                PlainJavaConnectionTestSuiteContext.EPSILON);
        Assert.assertEquals("coeff value is wrong @(row=1,col=1)", 0.4155128, matrix.getValueAt(1, 1).asDouble(),
                PlainJavaConnectionTestSuiteContext.EPSILON);
        Assert.assertEquals("coeff value is wrong @(row=1,col=2)", 9.463990, matrix.getValueAt(1, 2).asDouble(),
                PlainJavaConnectionTestSuiteContext.EPSILON);
        Assert.assertEquals("coeff value is wrong @(row=1,col=3)", 1.489836e-12, matrix.getValueAt(1, 3).asDouble(),
                PlainJavaConnectionTestSuiteContext.EPSILON);
    }

    static class Library {
        boolean active;
        String name;
        String lib;
        String desc;

        @Override
        public String toString() {
            return new StringBuffer().append("[name=").append(name).append(",lib=").append(lib).append(",desc=").append(desc)
                    .append(active ? ",ACTIVE" : "").append("]").toString();
        }
    }

    @Test
    public void testAssignString() {
        final ITwoWayConnection connection = PlainJavaConnectionTestSuiteContext.getInstance().acquireAssignableRConnection();
        connection.assign("mystring", "mystring");
        final IREXP myString = connection.eval("mystring");
        Assert.assertNotNull(myString);
        Assert.assertEquals("wrong type", IREXPConstants.XT_STR, myString.getType());
        String fromR = myString.asString();
        Assert.assertEquals("mystring", fromR);
    }

    @Test
    public void testAssignDoubleArray() {
        final ITwoWayConnection connection = PlainJavaConnectionTestSuiteContext.getInstance().acquireAssignableRConnection();
        connection.assign("mydoubles", new double[] { 1.0, 2.0, 3.0 });
        final IREXP myDoubles = connection.eval("mydoubles");
        Assert.assertNotNull(myDoubles);
        Assert.assertEquals("wrong type", IREXPConstants.XT_ARRAY_DOUBLE, myDoubles.getType());
        double[] fromR = myDoubles.asDoubleArray();
        Assert.assertEquals("wrong size", 3, fromR.length);
        Assert.assertEquals(1.0, fromR[0], PlainJavaConnectionTestSuiteContext.EPSILON);
        Assert.assertEquals(2.0, fromR[1], PlainJavaConnectionTestSuiteContext.EPSILON);
        Assert.assertEquals(3.0, fromR[2], PlainJavaConnectionTestSuiteContext.EPSILON);
    }

    @Test
    public void testAssignIntegerArray() {
        final ITwoWayConnection connection = PlainJavaConnectionTestSuiteContext.getInstance().acquireAssignableRConnection();
        connection.assign("myintegers", new int[] { 1, 2, 3 });
        final IREXP myIntegers = connection.eval("myintegers");
        Assert.assertNotNull(myIntegers);
        Assert.assertEquals("wrong type", IREXPConstants.XT_ARRAY_INT, myIntegers.getType());
        int[] fromR = myIntegers.asIntArray();
        Assert.assertEquals("wrong size", 3, fromR.length);
        Assert.assertEquals("wrong #1", 1, fromR[0]);
        Assert.assertEquals("wrong #2", 2, fromR[1]);
        Assert.assertEquals("wrong #3", 3, fromR[2]);
    }

    @Test
    public void testAssignStringArray() {
        final ITwoWayConnection connection = PlainJavaConnectionTestSuiteContext.getInstance().acquireAssignableRConnection();
        connection.assign("mystrings", new String[] { "1", "2", "3" });
        final IREXP myStrings = connection.eval("mystrings");
        Assert.assertNotNull(myStrings);
        Assert.assertEquals("wrong type", IREXPConstants.XT_ARRAY_STR, myStrings.getType());
        String[] fromR = myStrings.asStringArray();
        Assert.assertEquals("wrong size", 3, myStrings.length());
        Assert.assertEquals("wrong #1", "1", fromR[0]);
        Assert.assertEquals("wrong #2", "2", fromR[1]);
        Assert.assertEquals("wrong #3", "3", fromR[2]);
    }
}
