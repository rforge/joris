package org.rosuda.util.calculator;


import org.rosuda.util.calculator.interpreter.Interpreter;
import org.rosuda.util.calculator.interpreter.Interpreter.CombiReferenceResolver;
import org.rosuda.util.calculator.parser.Tokenizer;
import org.rosuda.util.calculator.parser.TreeNode;
import org.rosuda.util.calculator.parser.TreeParser;

public class Calculator {

	public static TreeNode createTree(final String expression) {
		return new TreeParser().parse(new Tokenizer(expression));
	}
	
	public static Number calculate(final String expression) {
		return evaluate(createTree(expression));
	}
	
	public static Number calculate(final String expression, final CombiReferenceResolver resolver) {
		return evaluate(createTree(expression), resolver);
	}
	
	public static Number evaluate(final TreeNode node) {
		return evaluate(node, null);
	}

	public static Number evaluate(final TreeNode node, final CombiReferenceResolver resolver) {
		return new Interpreter().eval(node, resolver);
	}
}
