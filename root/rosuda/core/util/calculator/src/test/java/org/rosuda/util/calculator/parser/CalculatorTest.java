package org.rosuda.util.calculator.parser;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.rosuda.util.calculator.Calculator;

public class CalculatorTest {

	private static final double EPSILON = 1e-8;

	@Test
	public void testBasicCalculus() {
		assertEquals(42.0, Calculator.calculate("6*7").doubleValue(), EPSILON);
		assertEquals(42.0, Calculator.calculate("(2+4)*7").doubleValue(), EPSILON);
		assertEquals(23.0, Calculator.calculate("2+3*7").doubleValue(), EPSILON);
		assertEquals(3.0, Calculator.calculate("2+(3/21)*7").doubleValue(), EPSILON);
	}
	
	@Test
	public void testEvaluateFunctions() {
		assertEquals(Math.exp(2.0 / 3.0), Calculator.calculate("exp(2/3)").doubleValue(),EPSILON);
		assertEquals(Math.log10(19.0), Calculator.calculate("log(19)").doubleValue(), EPSILON);
		assertEquals(Math.log(19.0), Calculator.calculate("ln(19)").doubleValue(), EPSILON);
		assertEquals(Math.exp(2 / 3) + Math.log10(19), Calculator.calculate(Math.exp(2/3)+"+"+Math.log10(19)).doubleValue(), EPSILON);	
		assertEquals(Math.pow(10,3), Calculator.calculate("10^3").doubleValue(), EPSILON);		
	}
	
	@Test
	public void testComplexExpressions() {
		assertEquals(Math.exp(2.0/3.0) + Math.log10(19.0), Calculator.calculate("exp(2/3)+log(19)").doubleValue(), EPSILON);
		assertEquals(Math.exp(2.0/3.0) * Math.log10(19.0),
				Calculator.calculate("exp(2/3)*log(19)").doubleValue(), EPSILON);
		assertEquals(Math.pow(Math.exp(2.0/3.0) , Math.log10(19.0)), Calculator.calculate("exp(2/3)^log(19)").doubleValue(),
				EPSILON);
	}
}
