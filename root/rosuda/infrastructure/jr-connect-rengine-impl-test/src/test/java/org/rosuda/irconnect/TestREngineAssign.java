/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rosuda.irconnect;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.rosuda.rengine.REngineConnectionFactory;

/**
 *
 * @author Ralf
 */
public class TestREngineAssign extends AbstractRTestCase {

    public static final double EPS = 0.000001;
	ITwoWayConnection connection;

    @Override
	protected void setUp() throws Exception {
		super.setUp();
        final Properties config = new Properties();
        config.load(TestWrappedEngine.class.getResourceAsStream("/org/rosuda/irconnect/config.properties"));
        final String configurationProperties = "/org/rosuda/irconnect/"+config.getProperty("mode")+".properties";
		final Properties testConfiguration = new Properties();
        testConfiguration.load(TestWrappedEngine.class.getResourceAsStream(configurationProperties));
        connection = new REngineConnectionFactory().createTwoWayConnection(testConfiguration);
	}

    @Override
	protected void tearDown() throws Exception {
		super.tearDown();
		connection.close();
//		try {
//			connection.shutdown();
//		} catch (final RServerException rse) {
//		}
	}

	public void testConnection() {
		assertNotNull("not connected", connection);
        assertTrue("no proxy !",Proxy.isProxyClass(connection.getClass()));
	}


	public void testList() {
		final IREXP libraryREXP = connection.eval(new StringBuffer().append(
				"try(").append("library()").append(")").toString());

		assertEquals(IREXP.XT_MAP, libraryREXP.getType());
		final IRMap namedRList = libraryREXP.asMap();
		final String[] keys = namedRList.keys();
		assertEquals("header", keys[0]);
		assertEquals("results", keys[1]);
		assertEquals("footer", keys[2]);
		final IREXP header = namedRList.at("header");
		assertTrue(header.getType() == IREXP.XT_NULL);
		final IREXP footer = namedRList.at("footer");
		assertTrue(footer.getType() == IREXP.XT_NULL);
		final IREXP results = namedRList.at("results");
		assertNotNull(results.dim());
		assertEquals(3, results.dim()[1]);
		assertTrue(results.getType() == IREXP.XT_ARRAY_STR);
		assertTrue(results.length() > 1);
		final String[] strings = results.asStringArray();
		final int rows = results.dim()[0];
		assertEquals(rows * 3, results.length());

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

	public void testString() {
		final IREXP libraryREXP = connection.eval(new StringBuffer().append(
		"try(").append("\"string\"").append(")").toString());
		assertNotNull(libraryREXP);
		assertEquals(IREXP.XT_STR, libraryREXP.getType());
		assertEquals("string", libraryREXP.asString());
	}

	public void testDouble() {
		final IREXP libraryREXP = connection.eval(new StringBuffer().append(
		"try(").append("1/4").append(")").toString());
		assertNotNull(libraryREXP);
		assertEquals(IREXP.XT_DOUBLE, libraryREXP.getType());
		assertEquals(0.25, libraryREXP.asDouble(),EPS);
	}

	public void testInt() {
		final IREXP libraryREXP = connection.eval(new StringBuffer().append(
		"try(").append("as.integer(1+4)").append(")").toString());
		assertNotNull(libraryREXP);
		assertEquals(IREXP.XT_INT, libraryREXP.getType());
		assertEquals(5, libraryREXP.asInt());
	}

	public void testBool() {
		final IREXP libraryREXP = connection.eval(new StringBuffer().append(
		"try(").append("1 == 1").append(")").toString());
		assertNotNull(libraryREXP);
		assertEquals(IREXP.XT_BOOL, libraryREXP.getType());
		assertEquals(true, libraryREXP.asBool().isTRUE());
	}

	public void testSymbol() {
		final IREXP libraryREXP = connection.eval(new StringBuffer().append(
		"try(").append("as.symbol(\"x\")").append(")").toString());
		assertNotNull(libraryREXP);
		assertEquals(IREXP.XT_SYM, libraryREXP.getType());
		assertEquals("x", libraryREXP.asSymbol().asString());
	}

	public void testFactor() {
		final IREXP libraryREXP = connection.eval(new StringBuffer().append(
		"try(").append("as.factor(c(1,1,1,2,2,1,3,2,3,1))").append(")").toString());
		assertNotNull(libraryREXP);
		assertEquals(IREXP.XT_FACTOR, libraryREXP.getType());
		final IRFactor factor = libraryREXP.asFactor();
		assertNotNull(factor);
		assertEquals(10, factor.size());
		assertEquals("1",factor.at(0));
		assertEquals("1",factor.at(1));
		assertEquals("1",factor.at(2));
		assertEquals("2",factor.at(3));
		assertEquals("2",factor.at(4));
		assertEquals("1",factor.at(5));
		assertEquals("3",factor.at(6));
		assertEquals("2",factor.at(7));
		assertEquals("3",factor.at(8));
		assertEquals("1",factor.at(9));
	}


	public void testStringArray() {
		final IREXP libraryREXP = connection.eval(new StringBuffer().append(
		"try(").append("c(\"string\",\"string2\")").append(")").toString());
		assertNotNull(libraryREXP);
		assertEquals(IREXP.XT_ARRAY_STR, libraryREXP.getType());
		final String[] sArray = libraryREXP.asStringArray();
		assertNotNull(sArray);
		assertEquals("string", sArray[0]);
		assertEquals("string2", sArray[1]);
	}

	public void testDoubleArray() {
		final IREXP libraryREXP = connection.eval(new StringBuffer().append(
		"try(").append("c(1/4,1/7)").append(")").toString());
		assertNotNull(libraryREXP);
		assertEquals(IREXP.XT_ARRAY_DOUBLE, libraryREXP.getType());
		final double[] dArray = libraryREXP.asDoubleArray();
		assertEquals(0.25, dArray[0], EPS);
		assertEquals(1.0/7.0, dArray[1], EPS);
	}

	public void testIntArray() {
		final IREXP libraryREXP = connection.eval(new StringBuffer().append(
		"try(").append("c(as.integer(1+4),as.integer(1+5))").append(")").toString());
		assertNotNull(libraryREXP);
		assertEquals(IREXP.XT_ARRAY_INT, libraryREXP.getType());
		final int[] iArray = libraryREXP.asIntArray();
		assertEquals(5, iArray[0]);
		assertEquals(6, iArray[1]);
	}

	public void testBoolArray() {
		final IREXP libraryREXP = connection.eval(new StringBuffer().append(
		"try(").append("c(1 == 1, 1 ==2)").append(")").toString());
		assertNotNull(libraryREXP);
		assertEquals(IREXP.XT_ARRAY_BOOL, libraryREXP.getType());
		IRBool[] bArray = libraryREXP.asBoolArray();
		assertEquals(true, bArray[0].isTRUE());
		assertEquals(true, bArray[1].isFALSE());
	}

	public void testVector() {
		final IREXP testREXP = connection.eval("c(a = as.integer(1), a2 = 2.12, b = 2 == 1, c = as.symbol(\"s\"))");
		assertNotNull(testREXP);
		//TODO distinguish between MAP and VECTOR
		assertEquals(IREXP.XT_MAP, testREXP.getType());
		final IRVector testVector = testREXP.asVector();
		assertNotNull(testVector);
		assertEquals(4, testVector.size());
		final IREXP at0 = testVector.at(0);
		assertNotNull(at0);
		assertEquals(at0.getType(), IREXP.XT_INT);

		final IREXP at1 = testVector.at(1);
		assertNotNull(at1);
		assertEquals(at1.getType(), IREXP.XT_DOUBLE);
	}

    public void testLibraries() {
		final IREXP testREXP =
			connection.eval(
				new StringBuffer()
					.append("try(")
					.append("library()")
					.append(")")
					.toString()
					);
			assertEquals("testREXP is not a List", IREXP.XT_MAP, testREXP.getType());
			final IRMap elementMap = testREXP.asMap();
            //debug names
			assertEquals("elementList.header is not XT_NULL", IREXP.XT_NULL, elementMap.at("header").getType());
			assertEquals("elementList.footer is not a XT_NULL", IREXP.XT_NULL, elementMap.at("footer").getType());
			assertEquals("elementList.keys #0 is not header", "header", elementMap.keys()[0]);
			assertEquals("elementList.keys #1 is not results", "results", elementMap.keys()[1]);
			assertEquals("elementList.keys #2 is not footer", "footer", elementMap.keys()[2]);

			assertEquals("elementList.getBody is not XT_ARRAY_STR", IREXP.XT_ARRAY_STR, elementMap.at("results").getType());
			final String[] listBody = elementMap.at("results").asStringArray();
            //TODO long list
			assertTrue("no of elements is not divisible by 3",listBody.length%3==0);
	}

     public void testMatrix() {
        final IREXP testREXP =
            connection.eval(
            new StringBuffer()
					.append("try(")
					.append("summary(lm(dist~speed,data=cars))$coefficients")
					.append(")")
					.toString()
					);
        assertEquals("textREXP is not a matrix", IREXP.XT_MATRIX, testREXP.getType());
        final IRMatrix matrix = testREXP.asMatrix();
        assertNotNull("conversion asMatrx failed!", matrix);
        assertEquals("not 2 rows", 2, matrix.getRows());
        assertEquals("not 4 columns", 4, matrix.getColumns());

        assertEquals("row #1 name wrong", "(Intercept)", matrix.getRowNameAt(0));
        assertEquals("row #2 name wrong", "speed", matrix.getRowNameAt(1));

        assertEquals("column #1 name wrong", "Estimate", matrix.getColumnNameAt(0));
        assertEquals("column #2 name wrong", "Std. Error", matrix.getColumnNameAt(1));
        assertEquals("column #3 name wrong", "t value", matrix.getColumnNameAt(2));
        assertEquals("column #4 name wrong", "Pr(>|t|)",matrix.getColumnNameAt(3));

        //just once the values
        for (int row = 0;row < matrix.getRows(); row++) {
            for (int col = 0;col < matrix.getColumns(); col++) {
                assertEquals("coeff type is wrong @(row="+row+",col="+col+")", matrix.getValueAt(row, col).getType(), IREXP.XT_DOUBLE);
            }
        }
        assertEquals("coeff value is wrong @(row=0,col=0)",-17.579095, matrix.getValueAt(0, 0).asDouble(),  TestWrappedEngine.EPS);
        assertEquals("coeff value is wrong @(row=0,col=1)",6.7584402, matrix.getValueAt(0, 1).asDouble(),  TestWrappedEngine.EPS);
        assertEquals("coeff value is wrong @(row=0,col=2)",-2.601058, matrix.getValueAt(0, 2).asDouble(),  TestWrappedEngine.EPS);
        assertEquals("coeff value is wrong @(row=0,col=3)",1.231882e-02, matrix.getValueAt(0, 3).asDouble(),  TestWrappedEngine.EPS);

        assertEquals("coeff value is wrong @(row=1,col=0)",3.932409, matrix.getValueAt(1, 0).asDouble(),  TestWrappedEngine.EPS);
        assertEquals("coeff value is wrong @(row=1,col=1)",0.4155128, matrix.getValueAt(1, 1).asDouble(),  TestWrappedEngine.EPS);
        assertEquals("coeff value is wrong @(row=1,col=2)",9.463990, matrix.getValueAt(1, 2).asDouble(),  TestWrappedEngine.EPS);
        assertEquals("coeff value is wrong @(row=1,col=3)",1.489836e-12, matrix.getValueAt(1, 3).asDouble(),  TestWrappedEngine.EPS);
    }

	static class Library {
		boolean active;
		String name;
		String lib;
		String desc;

        @Override
		public String toString() {
			return new StringBuffer().append("[name=").append(name).append(
					",lib=").append(lib).append(",desc=").append(desc).append(
					active ? ",ACTIVE" : "").append("]").toString();
		}
	}

    public void testAssignString() {
        connection.assign("mystring", "mystring");
        final IREXP myString = connection.eval("mystring");
        assertNotNull(myString);
        assertEquals("wrong type", IREXPConstants.XT_STR, myString.getType());
        String fromR = myString.asString();
        assertEquals("mystring", fromR);
    }

    public void testAssignDoubleArray() {
        connection.assign("mydoubles", new double[]{1.0,2.0,3.0});
        final IREXP myDoubles = connection.eval("mydoubles");
        assertNotNull(myDoubles);
        assertEquals("wrong type", IREXPConstants.XT_ARRAY_DOUBLE, myDoubles.getType());
        double[] fromR = myDoubles.asDoubleArray();
        assertEquals("wrong size", 3, fromR.length);
        assertEquals("wrong #1", 1.0, fromR[0]);
        assertEquals("wrong #2", 2.0, fromR[1]);
        assertEquals("wrong #3", 3.0, fromR[2]);
    }
    
    public void testAssignIntegerArray() {
        connection.assign("myintegers", new int[]{1,2,3});
        final IREXP myIntegers = connection.eval("myintegers");
        assertNotNull(myIntegers);
        assertEquals("wrong type", IREXPConstants.XT_ARRAY_INT, myIntegers.getType());
        int[] fromR = myIntegers.asIntArray();
        assertEquals("wrong size", 3, fromR.length);
        assertEquals("wrong #1", 1, fromR[0]);
        assertEquals("wrong #2", 2, fromR[1]);
        assertEquals("wrong #3", 3, fromR[2]);
    }

    public void testAssignStringArray() {
        connection.assign("mystrings", new String[]{"1","2","3"});
        final IREXP myStrings = connection.eval("mystrings");
        assertNotNull(myStrings);
        assertEquals("wrong type", IREXPConstants.XT_ARRAY_STR, myStrings.getType());
        String[] fromR = myStrings.asStringArray();
        assertEquals("wrong size", 3, myStrings.length());
        assertEquals("wrong #1", "1", fromR[0]);
        assertEquals("wrong #2", "2", fromR[1]);
        assertEquals("wrong #3", "3", fromR[2]);
    }
}
