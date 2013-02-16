package org.rosuda.irconnect.output;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.rosuda.irconnect.AREXP;
import org.rosuda.irconnect.IRBool;
import org.rosuda.irconnect.IREXP;
import org.rosuda.irconnect.IRFactor;
import org.rosuda.irconnect.IRList;
import org.rosuda.irconnect.IRMap;
import org.rosuda.irconnect.IRMatrix;
import org.rosuda.irconnect.IRVector;

public class IREXPFormatterTest {

    private IREXP rexp;
   
    private ObjectFormatter formatter;

    @Before
    public void setUp() {
	rexp = mock(IREXP.class);
	Locale.setDefault(Locale.US);
	formatter = new ObjectFormatter();
	
    }

    @Test
    public void formatArrayBoolean() {
	final IRBool irBool = irBool();
	when(rexp.getType()).thenReturn(IREXP.XT_ARRAY_BOOL);
	when(rexp.asBoolArray()).thenReturn(new IRBool[] { irBool, irBool });
	assertEquals("[true, true]", formatter.format(rexp));
    }

    @Test
    public void formatArrayBooleanUA() {
	final IRBool irBool = irBool();
	when(rexp.getType()).thenReturn(IREXP.XT_ARRAY_BOOL_UA);
	when(rexp.asBoolArray()).thenReturn(new IRBool[] { irBool, irBool });
	assertEquals("[true, true]", formatter.format(rexp));
    }

    @Test
    public void formatArrayDouble() {
	when(rexp.getType()).thenReturn(IREXP.XT_ARRAY_DOUBLE);
	when(rexp.asDoubleArray()).thenReturn(new double[] { Double.NaN, Double.NEGATIVE_INFINITY, Double.MAX_EXPONENT });
	assertEquals("[Not Numeric, Infinity, 1023.00]", formatter.format(rexp));
    }

    @Test
    public void formatArrayInt() {
	when(rexp.getType()).thenReturn(IREXP.XT_ARRAY_INT);
	when(rexp.asIntArray()).thenReturn(new int[] { 1, 2, 3, 4 });
	assertEquals("[1, 2, 3, 4]", formatter.format(rexp));
    }

    @Test
    public void formatArrayString() {
	when(rexp.getType()).thenReturn(IREXP.XT_ARRAY_STR);
	when(rexp.asStringArray()).thenReturn(new String[] { String.class.getSimpleName(), String.class.getSimpleName() });
	assertEquals("[String, String]", formatter.format(rexp));
    }

    @Test
    public void formatBool() {
	final IRBool irBool = irBool();
	when(rexp.getType()).thenReturn(IREXP.XT_BOOL);
	when(rexp.asBool()).thenReturn(irBool);
	assertEquals("true", formatter.format(rexp));
    }

    @Test
    public void formatClos() {
	when(rexp.getType()).thenReturn(IREXP.XT_CLOS);
	when(rexp.asString()).thenReturn("Closure");
	assertEquals("No formatter for type \"IREXP.XT_CLOS\" has been found.", formatter.format(rexp));
    }

    @Test
    public void formatDouble() {
	when(rexp.getType()).thenReturn(IREXP.XT_DOUBLE);
	when(rexp.asDouble()).thenReturn(Double.NaN);
	assertEquals("Not Numeric", formatter.format(rexp));
    }

    @Test
    public void formatFactor() {
	final IRFactor factor = mock(IRFactor.class);
	when(rexp.getType()).thenReturn(IREXP.XT_FACTOR);
	when(rexp.asFactor()).thenReturn(factor);
	when(factor.size()).thenReturn(2);
	when(factor.at(eq(0))).thenReturn("ONE");
	when(factor.at(eq(1))).thenReturn("TWO");
	assertEquals("[ONE, TWO]", formatter.format(rexp));
    }

    @Test
    public void formatInteger() {
	when(rexp.getType()).thenReturn(IREXP.XT_INT);
	when(rexp.asInt()).thenReturn(Integer.MAX_VALUE);
	assertEquals(Integer.toString(Integer.MAX_VALUE), formatter.format(rexp));
    }

    @Test
    public void formatLang() {
	when(rexp.getType()).thenReturn(IREXP.XT_LANG);
	assertEquals("No formatter for type \"IREXP.XT_LANG\" has been found.", formatter.format(rexp));
    }

    @Test
    public void formatList() {
	when(rexp.getType()).thenReturn(IREXP.XT_LIST);
	IRList irList = mock(IRList.class);
	when(rexp.asList()).thenReturn(irList);
	when(irList.keys()).thenReturn(new String[] { "1", "2" });
	when(irList.at(anyString())).thenReturn(null);
	assertEquals("[null, null]", formatter.format(rexp));
    }

    @Test
    public void formatMap() {
	when(rexp.getType()).thenReturn(IREXP.XT_MAP);
	IRMap irMap = mock(IRMap.class);
	when(rexp.asMap()).thenReturn(irMap);
	when(irMap.keys()).thenReturn(new String[] { "a", "b" });
	when(irMap.at(anyString())).thenReturn(null);
	assertEquals("[null, null]", formatter.format(rexp));
    }

    @Test
    public void formatMatrix() {
	when(rexp.getType()).thenReturn(IREXP.XT_MATRIX);
	IRMatrix irMatrix = mock(IRMatrix.class);
	when(rexp.asMatrix()).thenReturn(irMatrix);
	when(irMatrix.getColumns()).thenReturn(2);
	when(irMatrix.getRows()).thenReturn(3);
	when(irMatrix.getRowNameAt(anyInt())).thenAnswer(new IntegerAnswer("row"));
	when(irMatrix.getColumnNameAt(anyInt())).thenAnswer(new IntegerAnswer("col"));
	when(irMatrix.getValueAt(anyInt(), anyInt())).thenReturn(new AREXP() {
	    public int getType() {
		return IREXP.XT_INT;
	    }

	    public int asInt() {
		return 1;
	    }
	});
	final String formattedMatrix = formatter.format(rexp);
	assertEquals(8, countTokens(formattedMatrix, "1"));
	assertEquals(2, countTokens(formattedMatrix, "col"));
	assertEquals(3, countTokens(formattedMatrix, "row"));
    }

    @Test
    public void formatNull() {
	when(rexp.getType()).thenReturn(IREXP.XT_NULL);
	assertEquals("No formatter for type \"IREXP.XT_NULL\" has been found.", formatter.format(rexp));
    }

    @Test
    public void formatString() {
	when(rexp.getType()).thenReturn(IREXP.XT_STR);
	when(rexp.asString()).thenReturn(String.class.getSimpleName());
	assertEquals(String.class.getSimpleName(), formatter.format(rexp));
    }

    @Test
    public void formatSymbol() {
	when(rexp.getType()).thenReturn(IREXP.XT_SYM);
	when(rexp.asSymbol()).thenReturn(null);
	assertEquals("null", formatter.format(rexp));
    }

    @Test
    public void formatUnknown() {
	when(rexp.getType()).thenReturn(IREXP.XT_UNKNOWN);
	assertEquals("No formatter for type \"IREXP.XT_UNKNOWN\" has been found.", formatter.format(rexp));
    }

    @Test
    public void formatVector() {
	when(rexp.getType()).thenReturn(IREXP.XT_VECTOR);
	IRVector irVector = mock(IRVector.class);
	when(rexp.asVector()).thenReturn(irVector);
	when(irVector.size()).thenReturn(3);
	when(irVector.at(anyInt())).thenReturn(null);
	assertEquals("[null, null, null]", formatter.format(rexp));
    }

    // -- helper
    private int countTokens(final String where, final String what) {
	int idx1 = where.indexOf(what);
	int count = 0;
	while (idx1 > 0) {
	    count++;
	    idx1 = where.indexOf(what, idx1 + 1);
	}
	return count;
    }
    
    private IRBool irBool() {
	final IRBool irBool = mock(IRBool.class);
	when(irBool.isFALSE()).thenReturn(false);
	when(irBool.isNA()).thenReturn(false);
	when(irBool.isTRUE()).thenReturn(true);
	return irBool;
    }

    private static class IntegerAnswer implements Answer<String> {
	private final String prefix;

	private IntegerAnswer(final String prefix) {
	    this.prefix = prefix;

	}

	@Override
	public String answer(InvocationOnMock invocation) throws Throwable {
	    return prefix + "-" + (1 + (Integer)invocation.getArguments()[0]);
	}
    }

}
