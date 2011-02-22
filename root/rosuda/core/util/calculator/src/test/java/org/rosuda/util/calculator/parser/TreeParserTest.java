package org.rosuda.util.calculator.parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;
import org.rosuda.util.calculator.Calculator;
import org.rosuda.util.calculator.interpreter.Interpreter.CombiReferenceResolver;
import org.rosuda.util.calculator.interpreter.Interpreter.LoopOnlyReferenceResolver;
import org.rosuda.util.calculator.interpreter.Interpreter.SingleValueReferenceResolver;
import org.rosuda.util.calculator.parser.TreeNode;
import org.rosuda.util.calculator.parser.TreeNode.Type;

public class TreeParserTest extends TestCase {

	public void testNumber() {
		assertEquals(42.0, Calculator.calculate("42"));
	}
	
	public void testTwoOps() {
		assertEquals(42.0, Calculator.calculate("20+22"));
		assertEquals(42.0, Calculator.calculate("6*7"));
		assertEquals(42.0, Calculator.calculate("84/2"));
		assertEquals(42.0, Calculator.calculate("80-38"));
	}

	public void testCompareCombiOpTree() {
		final TreeNode root = TreeNode.buildStructure(TreeNode.Type.ADD);
		root.addChild(TreeNode.create(TreeNode.Type.VALUE, 20.0));
		root.addChild(TreeNode.create(TreeNode.Type.VALUE, 10.0));
		root.addChild(TreeNode.create(TreeNode.Type.VALUE, 8.0));
		root.addChild(TreeNode.create(TreeNode.Type.VALUE, 4.0));
		final TreeNode createdRoot = Calculator.createTree("20+10+8+4");
		assertEquals(42.0, Calculator.evaluate(root));
		assertEquals(42.0, Calculator.evaluate(createdRoot));
		assertTrue(root.equals(createdRoot));
	}
	
	public void testMultipleSameOps() {
		assertEquals(42.0, Calculator.calculate("20+18+4"));
		assertEquals(42.0, Calculator.calculate("2*3*7"));
		assertEquals(42.0, Calculator.calculate("80-20-18"));
		assertEquals(42.0, Calculator.calculate("168/2/2"));
	}
	
	public void testMathRules() {
		assertEquals(25.0, Calculator.calculate("20+10/2"));
		assertEquals(4.0, Calculator.calculate("20/10+2"));
		assertEquals(1.0, Calculator.calculate("20/10/2"));
	}
	
	public void testCalculate() {
		//(10+20)/5
		final TreeNode root = TreeNode.buildStructure(TreeNode.Type.DIV);	
		final TreeNode bracket = TreeNode.buildStructure(TreeNode.Type.BRACKET);
		final TreeNode sum = TreeNode.buildStructure(TreeNode.Type.ADD);
		bracket.addChild(sum);
		sum.addChild(TreeNode.create(TreeNode.Type.VALUE, 10.0));
		sum.addChild(TreeNode.create(TreeNode.Type.VALUE, 20.0));
		root.addChild(bracket);
		root.addChild(TreeNode.create(TreeNode.Type.VALUE, 5.0));
		final Number result = Calculator.evaluate(root);
		assertNotNull(result);
		assertEquals(6.0, result.doubleValue());
		//compareTree
		final TreeNode createdRoot = Calculator.createTree("(10+20)/5");
		assertEquals(6.0, Calculator.evaluate(createdRoot).doubleValue());
		assertTrue(root.equals(createdRoot));
		
		//10+20/5
		final TreeNode noBracketsRoot = TreeNode.buildStructure(TreeNode.Type.ADD);
		noBracketsRoot.addChild(TreeNode.create(TreeNode.Type.VALUE, 10.0));
		final TreeNode noBracketsDiv = TreeNode.buildStructure(TreeNode.Type.DIV);
		noBracketsRoot.addChild(noBracketsDiv);
		noBracketsDiv.addChild(TreeNode.create(TreeNode.Type.VALUE, 20.0));
		noBracketsDiv.addChild(TreeNode.create(TreeNode.Type.VALUE, 5.0));
		final TreeNode noBracketsCreated = Calculator.createTree("10+20/5");
		assertEquals(14.0, Calculator.evaluate(noBracketsCreated).doubleValue());
		assertTrue(noBracketsRoot.equals(noBracketsCreated));
	}
	
	
	public void testNoBrackets() {
		final TreeNode result_1_p_2_p_3_p_4 = Calculator.createTree("1+2+3+4");
		assertNotNull (result_1_p_2_p_3_p_4);
		assertEquals(TreeNode.Type.ADD,result_1_p_2_p_3_p_4.getType());
		//interpret:
		final Number sum = Calculator.evaluate(result_1_p_2_p_3_p_4);
		assertNotNull(sum);
		assertEquals(10.0, sum.doubleValue());
		assertEquals(4,result_1_p_2_p_3_p_4.getChildren().size());
		final TreeNode result_1_m_2_m_3_m_4 = Calculator.createTree("1-2-3-4");
		assertNotNull (result_1_m_2_m_3_m_4);
		assertEquals(TreeNode.Type.SUB,result_1_m_2_m_3_m_4.getType());
		//interpret:
		final Number difference = Calculator.evaluate(result_1_m_2_m_3_m_4);
		assertNotNull(difference);
		assertEquals(-8.0, difference.doubleValue());
	}
	
	public void testWildBrackets() {
		assertEquals(4.0, Calculator.calculate("(10+3-1)/(1+1+1)"));
		assertEquals(15.0, Calculator.calculate("(10+(10/(1+1))"));
		assertEquals(4.0, Calculator.calculate("(1+3+20)/(2*3)"));
		assertEquals(0.5, Calculator.calculate("(10/5)/(5-1)"));
		assertEquals(4.0, Calculator.calculate("((1+3+20)/(2*3))"));
		assertEquals(0.5, Calculator.calculate("((10/5)/(5-1))"));	
		assertEquals(8.0, Calculator.calculate("((1+3+20)/(2*3))/((10/5)/(5-1))"));
	}
	
	public void testParseReference() {
		final TreeNode parsedReference = Calculator.createTree("10*(${token}+3)");
		assertEquals(50.0, Calculator.evaluate(parsedReference, new SingleValueReferenceResolver(){
			public Number resolveRef(final String id) {
				assertEquals("token",id);
				return 2.0;
			}}));
	}
	
	public void testFunction() {
		assertEquals(1.0, Calculator.calculate("log(10)"));
		assertEquals(1.0, Calculator.calculate("ld(2)"));
		assertEquals(Math.log(30.0), Calculator.calculate("ln(10+10+10)"));
		assertEquals(1.0/Math.log(10.0), Calculator.calculate("1/ln(10)"));		
		assertEquals(16.0, Calculator.calculate("ld(65536)"));
		assertEquals(4.0, Calculator.calculate("sqrt(16)"));
		assertEquals(4.0, Calculator.calculate("min(4,16)"));
		
		assertEquals(1.0, Calculator.calculate("ld(sqrt(min(4,16)))"));
		
	}

	public void testCombinedFunction() {
		final TreeNode rootNode = TreeNode.buildStructure(TreeNode.Type.DIV);
		final TreeNode leftChild = TreeNode.create(TreeNode.Type.FUNCTION,"log");
		final TreeNode rightChild = TreeNode.create(TreeNode.Type.FUNCTION,"ld");
		rootNode.addChild(leftChild); rootNode.addChild(rightChild);
		final TreeNode leftBracket = TreeNode.buildStructure(Type.BRACKET);
		final TreeNode rightBracket = TreeNode.buildStructure(Type.BRACKET);
		leftChild.addChild(leftBracket); rightChild.addChild(rightBracket);
		leftBracket.addChild(TreeNode.create(TreeNode.Type.VALUE, 10)); 
		rightBracket.addChild(TreeNode.create(TreeNode.Type.VALUE, 4));
		assertEquals(0.5, Calculator.evaluate(rootNode));
		final TreeNode combinedTree = Calculator.createTree("log(10)/ld(4)");
		assertEquals(0.5, Calculator.evaluate(combinedTree));
	}
	
	public void testLoopFunction() {
		assertEquals(55.0, Calculator.calculate("csum(${myTokens})", new LoopOnlyReferenceResolver(){
			int count = 0;
			public Number resolveLoopRef(final String id) {
				assertTrue("myTokens".equals(id));
				return count;
			}
			public boolean next() {
				return count ++ < 10;
			}
			public Number resolveRef(final String id) {
				return null;
			}
			
		}));
		
		assertEquals(5.5, Calculator.calculate("cmean(${myTokens})", new LoopOnlyReferenceResolver(){
			int count = 0;
			public Number resolveLoopRef(final String id) {
				assertTrue("myTokens".equals(id));
				return count;
			}
			public boolean next() {
				return count ++ < 10;
			}
			
		}));	
		
		assertEquals(1.0, Calculator.calculate("cmin(${myTokens})", new LoopOnlyReferenceResolver(){
			int count = 0;
			public Number resolveLoopRef(final String id) {
				assertTrue("myTokens".equals(id));
				return count;
			}
			public boolean next() {
				return count ++ < 10;
			}
			
		}));
		
		assertEquals(10.0, Calculator.calculate("cmax(${myTokens})", new LoopOnlyReferenceResolver(){
			int count = 0;
			public Number resolveLoopRef(final String id) {
				assertTrue("myTokens".equals(id));
				return count;
			}
			public boolean next() {
				return count ++ < 10;
			}
			
		}));
	}
	
	public void testCombinations() {
		assertEquals(5.5, Calculator.calculate("cmean(ld(${myTokens}))", new LoopOnlyReferenceResolver(){
			int count = 0;
			public Number resolveLoopRef(final String id) {
				assertTrue("myTokens".equals(id));
				return Math.pow(2.0, count);
			}
			public boolean next() {
				return count ++ < 10;
			}
			
		}));	
		
		assertEquals(55.0, Calculator.calculate("csum(ld(${myTokens}))", new LoopOnlyReferenceResolver(){
			int count = 0;
			public Number resolveLoopRef(final String id) {
				assertTrue("myTokens".equals(id));
				return Math.pow(2.0, count);
			}
			public boolean next() {
				return count ++ < 10;
			}
			
		}));
		
		
		assertEquals(55.0, Calculator.calculate("5*${outerToken}*cmean(ld(${myTokens}))", new CombiReferenceResolver(){
			int count = 0;
			public Number resolveLoopRef(final String id) {
				assertTrue("myTokens".equals(id));
				return Math.pow(2.0, count);
			}
			public boolean next() {
				return count ++ < 10;
			}
			public Number resolveRef(String id) {
				assertTrue("outerToken".equals(id));
				return 2.0;
			}
			
		}));
	}
	
	public void testNegativeNumbers() {
		assertEquals(-0.5, Calculator.calculate("-0.5"));
		assertEquals(Math.exp(-50.0), Calculator.calculate("exp(-0.5*100)"));
	}
	
	public void testInnerNodes() {
		final List<Number> aics = new ArrayList<Number>();
		aics.add(482.0);
		aics.add(478.0);
		aics.add(479.0);
		
		assertEquals(4.0, Calculator.calculate("${AIC}-cmin(${AIC}", new CombiReferenceResolver(){
			final Iterator<Number> aicIter = aics.iterator();
			public Number resolveRef(final String id) {
				return 482.0;
			}

			public boolean next() {
				return aicIter.hasNext();
			}

			public Number resolveLoopRef(final String id) {
				return aicIter.next();
			}
		}));

		assertEquals((Math.exp(-0.5*4)+Math.exp(-0.5*0)+Math.exp(-0.5*1)), Calculator.calculate("csum(exp(-0.5*${@AICdiff}))", new CombiReferenceResolver(){
			final Iterator<Number> aicIter = aics.iterator();
			public Number resolveRef(final String id) {
				return 482.0-478.0;
			}

			public boolean next() {
				return aicIter.hasNext();
			}

			public Number resolveLoopRef(final String id) {
				return aicIter.next().doubleValue()-478.0;
			}
		}));

		assertEquals(Math.exp(-0.5*4), Calculator.calculate("exp(-0.5*${@AICdiff})", new CombiReferenceResolver(){
			final Iterator<Number> aicIter = aics.iterator();
			public Number resolveRef(final String id) {
				return 482.0-478.0;
			}

			public boolean next() {
				return aicIter.hasNext();
			}

			public Number resolveLoopRef(final String id) {
				return aicIter.next().doubleValue()-478.0;
			}
		}));
		
	}
	public void testEvidenceRations() {
		final List<Number> aics = new ArrayList<Number>();
		aics.add(482.0);
		aics.add(478.0);
		aics.add(479.0);
	
	//	aics.add(499.0);
	//	aics.add(489.0);
	//	aics.add(479.0);
			
		//check tree !!
		final TreeNode rootNode = TreeNode.buildStructure(TreeNode.Type.DIV);
		final TreeNode leftChild = TreeNode.create(TreeNode.Type.FUNCTION,"exp");
		final TreeNode rightChild = TreeNode.create(TreeNode.Type.FUNCTION,"csum");
		rootNode.addChild(leftChild); rootNode.addChild(rightChild);
		final TreeNode leftBracket = TreeNode.buildStructure(TreeNode.Type.BRACKET);
		leftChild.addChild(leftBracket);
		final TreeNode leftOp = TreeNode.buildStructure(TreeNode.Type.MUL);
		leftBracket.addChild(leftOp);
		leftOp.addChild(TreeNode.create(TreeNode.Type.VALUE, -0.5));
		leftOp.addChild(TreeNode.create(TreeNode.Type.REFERENCE, "@AICdiff"));
		
		final TreeNode rightCsumBracket = TreeNode.buildStructure(TreeNode.Type.BRACKET);
		rightChild.addChild(rightCsumBracket);
		final TreeNode rightExp = TreeNode.create(TreeNode.Type.FUNCTION,"exp");
		rightCsumBracket.addChild(rightExp);
		final TreeNode rightBracket = TreeNode.buildStructure(TreeNode.Type.BRACKET);
		rightExp.addChild(rightBracket);
		final TreeNode rightMul = TreeNode.buildStructure(TreeNode.Type.MUL);
		rightBracket.addChild(rightMul);
		rightMul.addChild(TreeNode.create(TreeNode.Type.VALUE, -0.5));
		rightMul.addChild(TreeNode.create(TreeNode.Type.REFERENCE, "@AICdiff"));
		
		assertEquals(Math.exp(-0.5*4)/(Math.exp(-0.5*4)+Math.exp(-0.5*0)+Math.exp(-0.5*1)), Calculator.evaluate(rootNode, new CombiReferenceResolver(){
			final Iterator<Number> aicIter = aics.iterator();
			public Number resolveRef(final String id) {		
				return 482.0-478.0;
			}

			public boolean next() {
				return aicIter.hasNext();
			}

			public Number resolveLoopRef(final String id) {
				return aicIter.next().doubleValue()-478.0;
			}
		}));
		final TreeNode aicWtNode = Calculator.createTree("exp(-0.5*${@AICdiff})/csum(exp(-0.5*${@AICdiff}))");
		//structural comparison
		//assertTrue("root structuture differs:\nreference = "+rootNode+"\ncompare   = "+aicWtNode, rootNode.equals(aicWtNode));
		assertEquals(Math.exp(-0.5*4)/(Math.exp(-0.5*4)+Math.exp(-0.5*0)+Math.exp(-0.5*1)), Calculator.evaluate(aicWtNode, new CombiReferenceResolver(){
			final Iterator<Number> aicIter = aics.iterator();
			public Number resolveRef(final String id) {		
				return 482.0-478.0;
			}

			public boolean next() {
				return aicIter.hasNext();
			}

			public Number resolveLoopRef(final String id) {
				return aicIter.next().doubleValue()-478.0;
			}
		}));
		
		//AICdiff ${AIC}-cmin(${AIC})
		//AICweig = exp(-0.5*${@AICdiff})/csum(exp(-0.5*${@AICdiff}))
		
	}


}
