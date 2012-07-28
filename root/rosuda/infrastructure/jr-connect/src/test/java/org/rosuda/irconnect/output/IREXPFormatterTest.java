package org.rosuda.irconnect.output;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import mockit.NonStrict;
import mockit.NonStrictExpectations;

import org.junit.Before;
import org.junit.Test;
import org.rosuda.irconnect.AREXP;
import org.rosuda.irconnect.IRBool;
import org.rosuda.irconnect.IREXP;
import org.rosuda.irconnect.IRFactor;
import org.rosuda.irconnect.IRList;
import org.rosuda.irconnect.IRMap;
import org.rosuda.irconnect.IRMatrix;
import org.rosuda.irconnect.IRVector;

public class IREXPFormatterTest {

	@NonStrict private IREXP rexp;
	@NonStrict private IRBool irBool;
	@NonStrict private IRList irList;
	@NonStrict private IRMap irMap;
	@NonStrict private IRMatrix irMatrix;
	@NonStrict private IRFactor irFactor;
	@NonStrict private IRVector irVector;
	
	private ObjectFormatter formatter;
	
	@Before
	public void setUp() {
		Locale.setDefault(Locale.US);
		formatter = new ObjectFormatter();
		new NonStrictExpectations() { 
			{
				irBool.isTRUE(); returns(true);
				irBool.isNA(); returns(false);
				irBool.isFALSE(); returns (false);
			}
		};
	}
	
	@Test 
	public void formatArrayBoolean() {
		new NonStrictExpectations() { 
			{
				rexp.getType(); returns (IREXP.XT_ARRAY_BOOL);
				rexp.asBoolArray(); returns (new IRBool[]{irBool, irBool});
			}
		};
		assertEquals("[true, true]", formatter.format(rexp));
	}

	@Test 
	public void formatArrayBooleanUA() {
		new NonStrictExpectations() { 
			{
				rexp.getType(); returns (IREXP.XT_ARRAY_BOOL_UA);
				rexp.asBoolArray(); returns (new IRBool[]{irBool, irBool});
			}
		};
		assertEquals("[true, true]", formatter.format(rexp));
	}

	@Test 
	public void formatArrayDouble() {
		new NonStrictExpectations() { 
			{
				rexp.getType(); returns (IREXP.XT_ARRAY_DOUBLE);
				rexp.asDoubleArray(); returns (new double[]{Double.NaN, Double.NEGATIVE_INFINITY, Double.MAX_EXPONENT});
			}
		};
		assertEquals("[Not Numeric, Infinity, 1023.00]", formatter.format(rexp));
	}

	@Test 
	public void formatArrayInt() {
		new NonStrictExpectations() { 
			{
				rexp.getType(); returns (IREXP.XT_ARRAY_INT);
				rexp.asIntArray(); returns (new int[]{1,2,3,4});
			}
		};
		assertEquals("[1, 2, 3, 4]", formatter.format(rexp));
	}

	@Test 
	public void formatArrayString() {
		new NonStrictExpectations() { 
			{
				rexp.getType(); returns (IREXP.XT_ARRAY_STR);
				rexp.asStringArray(); returns (new String[]{String.class.getSimpleName(), String.class.getSimpleName()});
			}
		};
		assertEquals("[String, String]", formatter.format(rexp));
	}

	@Test 
	public void formatBool() {
		new NonStrictExpectations() { 
			{
				rexp.getType(); returns (IREXP.XT_BOOL);
				rexp.asBool(); returns (irBool);
			}
		};
		assertEquals("true", formatter.format(rexp));
	}

	@Test 
	public void formatClos() {
		new NonStrictExpectations() { 
			{
				rexp.getType(); returns (IREXP.XT_CLOS);
				//TODO check how an R closure behaves
				rexp.asString(); returns ("Closure");
			}
		};
		assertEquals("No formatter for type \"IREXP.XT_CLOS\" has been found.", formatter.format(rexp));
	}
	
	@Test 
	public void formatDouble() {
		new NonStrictExpectations() { 
			{
				rexp.getType(); returns (IREXP.XT_DOUBLE);
				rexp.asDouble(); returns (Double.NaN);
			}
		};
		assertEquals("Not Numeric", formatter.format(rexp));
	}

	@Test 
	public void formatFactor() {
		new NonStrictExpectations() { 
			{
				rexp.getType(); returns (IREXP.XT_FACTOR);
				rexp.asFactor(); returns (irFactor);
				irFactor.size(); returns (2);
				irFactor.at(0); returns ("ONE");
				irFactor.at(1); returns ("TWO");	
			}
		};
		assertEquals("[ONE, TWO]", formatter.format(rexp));
	}

	@Test 
	public void formatInteger() {
		new NonStrictExpectations() { 
			{
				rexp.getType(); returns (IREXP.XT_INT);
				rexp.asInt(); returns (Integer.MAX_VALUE);
			}
		};
		assertEquals(Integer.toString(Integer.MAX_VALUE), formatter.format(rexp));
	}

	@Test 
	public void formatLang() {
		new NonStrictExpectations() { 
			{
				rexp.getType(); returns (IREXP.XT_LANG);
			}
		};
		assertEquals("No formatter for type \"IREXP.XT_LANG\" has been found.", formatter.format(rexp));
	}

	@Test 
	public void formatList() {
		new NonStrictExpectations() { 
			{
				rexp.getType(); returns (IREXP.XT_LIST);
				rexp.asList(); returns (irList);
				irList.keys(); returns (new String[]{"1","2"});
				irList.at(anyString); returns (null);
			}
		};
		assertEquals("[null, null]", formatter.format(rexp));
	}
	
	@Test 
	public void formatMap() {
		new NonStrictExpectations() { 
			{
				rexp.getType(); returns (IREXP.XT_MAP);
				rexp.asMap(); returns (irMap);
				irMap.keys(); returns (new String[]{"a","b"});
				irMap.at(anyString); returns (null);
			}
		};
		assertEquals("[null, null]", formatter.format(rexp));
	}

	@Test 
	public void formatMatrix() {
		new NonStrictExpectations() { 
			{
				rexp.getType(); returns (IREXP.XT_MATRIX);
				rexp.asMatrix(); returns (irMatrix);
				irMatrix.getColumns(); returns (2);
				irMatrix.getRows(); returns (3);
				irMatrix.getColumnNameAt(anyInt); returns ("col");
				irMatrix.getRowNameAt(anyInt); returns("row");
				irMatrix.getValueAt(anyInt, anyInt); returns (new AREXP() {
					public int getType() {return IREXP.XT_INT;}
					public int asInt() { return 1;}
				});
			}
		};
		//TODO count tokens
		final String formattedMatrix = formatter.format(rexp);
		assertEquals(6, countTokens(formattedMatrix, "1"));
		assertEquals(2, countTokens(formattedMatrix, "col"));
		assertEquals(3, countTokens(formattedMatrix, "row"));		
	}
	
	private int countTokens(final String where, final String what) {
		int idx1 = where.indexOf(what);
		int count = 0;
		while (idx1 > 0) {
			count ++;
			idx1 = where.indexOf(what, idx1 + 1);
		}
		return count;
	}
	
	@Test 
	public void formatNull() {
		new NonStrictExpectations() { 
			{
				rexp.getType(); returns (IREXP.XT_NULL);
			}
		};
		assertEquals("No formatter for type \"IREXP.XT_NULL\" has been found.", formatter.format(rexp));
	}


	@Test 
	public void formatString() {
		new NonStrictExpectations() { 
			{
				rexp.getType(); returns (IREXP.XT_STR);
				rexp.asString(); returns (String.class.getSimpleName());
			}
		};
		assertEquals(String.class.getSimpleName(), formatter.format(rexp));
	}
	
	@Test 
	public void formatSymbol() {
		new NonStrictExpectations() { 
			{
				rexp.getType(); returns (IREXP.XT_SYM);
				rexp.asSymbol(); returns (null);
			}
		};
		assertEquals("null", formatter.format(rexp));
	}
	
	@Test 
	public void formatUnknown() {
		new NonStrictExpectations() { 
			{
				rexp.getType(); returns (IREXP.XT_UNKNOWN);
			}
		};
		assertEquals("No formatter for type \"IREXP.XT_UNKNOWN\" has been found.", formatter.format(rexp));
	}

	@Test 
	public void formatVector() {
		new NonStrictExpectations() { 
			{
				rexp.getType(); returns (IREXP.XT_VECTOR);
				rexp.asVector(); returns (irVector);
				irVector.size(); returns (3);
				irVector.at(anyInt); returns (null);
			}
		};
		assertEquals("[null, null, null]", formatter.format(rexp));
	}


}
