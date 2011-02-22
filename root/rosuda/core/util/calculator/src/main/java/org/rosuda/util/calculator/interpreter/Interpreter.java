package org.rosuda.util.calculator.interpreter;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.rosuda.util.calculator.parser.TreeNode;
import org.rosuda.util.calculator.parser.TreeNode.Type;

public class Interpreter {

	public static interface ReferenceResolver {
		public abstract Number resolveRef(final String id);
	}
	
	/**
	 * required if functions as "sum" "mean" "median" should be performed
	 * @author ralfseger
	 *
	 */
	public static interface LoopReferenceResolver{
		/** resolves the no at the loop item **/
		public abstract Number resolveLoopRef(final String id);
		
		public abstract boolean next();
	}
	
	public static interface CombiReferenceResolver extends ReferenceResolver, LoopReferenceResolver{}
	
	public static abstract class LoopOnlyReferenceResolver implements CombiReferenceResolver {
		public Number resolveRef(final String id) {
			return null;
		}
	}
	
	public static abstract class SingleValueReferenceResolver implements CombiReferenceResolver {
		public Number resolveLoopRef(final String id) {
			return null;
		}
		
		public boolean next() {
			return false;
		}
	}
	
	private static class LoopSingleExtractionResolver implements ReferenceResolver {

		private final LoopReferenceResolver loopResolver;
		
		private LoopSingleExtractionResolver(final LoopReferenceResolver loopResolver) {
			this.loopResolver = loopResolver;
		}
		
		public Number resolveRef(String id) {
			return loopResolver.resolveLoopRef(id);
		}
		
	}
	
	
	public static class InterpreterException extends RuntimeException {
		/**
		 * 
		 */
		private static final long serialVersionUID = 2101508856879618102L;
		
		private final TreeNode node;
		
		InterpreterException(final String string, final TreeNode node) {
			super(string);
			this.node = node;
		}
	
		@Override
		public String getMessage() {
			return super.getMessage()+"@node("+node.getType()+",value="+node.getValue()+",#children="+node.getChildren().size()+")";
		}
	}
	
	/**
	 * 
	 * @param node
	 * @param resolver required for tokens, for iterations a special brand LoopReferenceResolver needs to be used !
	 * @return
	 * @throws InterpreterException
	 */
	public Number eval(final TreeNode node, final ReferenceResolver resolver) throws InterpreterException
	   {  
		switch (node.getType()) {
			case BRACKET : return eval(node.getChildren().get(0), resolver);
			case REFERENCE : return resolver.resolveRef((String)node.getValue());
			case VALUE : return (Number)(node.getValue());
	   		case ADD: {
	   			double accumulator = 0.0;
	   			for (TreeNode child : node.getChildren()) {
	   				final Number value = eval(child, resolver);
	   				if (value!=null)
	   					accumulator += value.doubleValue();
	   			}
	   			return accumulator;
	   		}
	   		case MUL: {
	   			double accumulator = 1.0;
	   			for (TreeNode child : node.getChildren()) {
	   				final Number value = eval(child, resolver);
	   				if (value!=null)
	   					accumulator *= value.doubleValue();
	   			}
	   			return accumulator;
	   		}
	   		case SUB: {
	   			final Iterator<TreeNode> children = node.getChildren().iterator();
	   			if (!children.hasNext())
	   				throw new InterpreterException("Illegal Operation", node);
	   			final Number base = eval(children.next(), resolver);
	   			if (base==null)
	   				return null;
	   			double accumulator = base.doubleValue();
	   			while (children.hasNext()) {
	   			    final Number value = eval(children.next(), resolver);			
	   				if (value!=null)
	   					accumulator -= value.doubleValue();
	   			}
	   			return accumulator;
	   		}
	   		case DIV: {
	   			final Iterator<TreeNode> children = node.getChildren().iterator();
	   			if (!children.hasNext())
	   				throw new InterpreterException("Illegal Operation", node);
	   			final Number base = eval(children.next(), resolver);
	   			if (base==null)
	   				return null;
	   			double accumulator = base.doubleValue();
	   			while (children.hasNext()) {
	   				final Number value = eval(children.next(), resolver);
	   				if (value!=null)
	   					accumulator /= value.doubleValue();
	   			}
	   			return accumulator;
	   		}
	   		case FUNCTION: {
	   			final Function function = functions.get(node.getValue());
	   			if (function==null)
	   				throw new UnsupportedOperationException("unknown function \""+node.getValue()+"\"");
	   			return function.evaluate(node, resolver);
	   		}
	   		default: 
	   			return null;
		}	
	}
	
	//functions
	
	public static interface Function {
		public abstract Number evaluate (final TreeNode node, final ReferenceResolver resolver);
	}
	
	static abstract class OneArgumentFunction implements Function {
		public final Number evaluate(final TreeNode node, final ReferenceResolver resolver) {
			if (node.getChildren().isEmpty()||node.getChildren().size()>1)
				throw new IllegalArgumentException("function \""+node.getValue()+"\" needs exactly one argument!");
			final Number value = new Interpreter().eval(node.getChildren().get(0), resolver);
			if (value == null)
				return null;
			return handleNumber(value);
		}
		abstract Number handleNumber(final Number number);
	}
	
	static abstract class TwoArgumentsFunction implements Function {
		public final Number evaluate(final TreeNode inNode, final ReferenceResolver resolver) {
			TreeNode childNode = inNode;
			if (childNode.getChildren().size()==1 && childNode.getChildren().get(0).getType().equals(Type.BRACKET))
				childNode = childNode.getChildren().get(0);
			if (childNode.getChildren().size()==1 && childNode.getChildren().get(0).getType().equals(Type.SEPARATOR))
				childNode = childNode.getChildren().get(0);
			if (childNode.getChildren().size()!=2)
				throw new IllegalArgumentException("function \""+inNode.getValue()+"\" needs exactly two arguments!");
			final Number arg0 = new Interpreter().eval(childNode.getChildren().get(0), resolver);			
			final Number arg1 = new Interpreter().eval(childNode.getChildren().get(1), resolver);
			if (arg0 == null || arg1 == null)
				return null;
			return handleNumber(arg0, arg1);
		}
		abstract Number handleNumber(final Number arg0, final Number arg1);
	}
	
	static abstract class OneArgumentLoopFunction implements Function {
		public final Number evaluate(final TreeNode node, final ReferenceResolver resolver) {
			if (!(resolver instanceof LoopReferenceResolver))
				throw new IllegalArgumentException("cannot use loop function without LoopReferenceResolver");
			final LoopReferenceResolver loopResolver = (LoopReferenceResolver) resolver;
			if (node.getChildren().size()!=1)
				throw new IllegalArgumentException("function \""+node.getValue()+"\" needs exactly one argument!");
			Number accumulator = getInitialAccumulator();
			while (loopResolver.next()) {
				final Number loopResult = new Interpreter().eval(node.getChildren().get(0), new LoopSingleExtractionResolver(loopResolver));
				accumulator = handleLoopNumber(accumulator, loopResult);
			}	
			return handleResult(accumulator);
		}
		Number getInitialAccumulator() {return 0.0;}
		abstract Number handleLoopNumber(final Number accumulator, final Number loopResult);
		Number handleResult(final Number accumulator) {return accumulator;}	
	}
	
	static final Function abs = new OneArgumentFunction()  {
		@Override Number handleNumber(final Number number) {
			return Math.abs(number.doubleValue());
		}};
	static final Function sqrt = new OneArgumentFunction()  {
		@Override Number handleNumber(final Number number) {
			return Math.sqrt(number.doubleValue());
		}};		
	static final Function log = new OneArgumentFunction()  {
		@Override Number handleNumber(final Number number) {
			return Math.log(number.doubleValue())/Math.log(10);
		}};	
	static final Function ln = new OneArgumentFunction()  {
		@Override Number handleNumber(final Number number) {
			return Math.log(number.doubleValue());
		}};	
	static final Function ld = new OneArgumentFunction()  {
		@Override Number handleNumber(final Number number) {
			return Math.log(number.doubleValue())/Math.log(2);
		}};	
	static final Function exp = new OneArgumentFunction()  {
		@Override Number handleNumber(final Number number) {
			return Math.exp(number.doubleValue());
		}};	
	static final Function min = new TwoArgumentsFunction()  {
		@Override Number handleNumber(final Number arg0, final Number arg1) {
			return Math.min(arg0.doubleValue(),arg1.doubleValue());
		}};	
	static final Function max = new TwoArgumentsFunction()  {
		@Override Number handleNumber(final Number arg0, final Number arg1) {
			return Math.max(arg0.doubleValue(),arg1.doubleValue());
		}};	
	
	static final Function csum = new OneArgumentLoopFunction()  {
		@Override Number handleLoopNumber(final Number accumulator, final Number loopResult) {
			if (loopResult==null)
				return accumulator;
			return accumulator.doubleValue()+loopResult.doubleValue();
		}
	};
	
	static final Function cmean = new OneArgumentLoopFunction()  {
		int count;
		@Override Number getInitialAccumulator() {
			//! static class !! reinitialize variables!
			count = 0;
			return 0.0;
		}
		@Override Number handleLoopNumber(final Number accumulator, final Number loopResult) {
			count ++;
			if (loopResult==null)
				return accumulator;
			return accumulator.doubleValue()+loopResult.doubleValue();
		}
		@Override
		Number handleResult(final Number accumulator) {
			return accumulator.doubleValue()/(double) count;
		}
	};
	
	static final Function cmin = new OneArgumentLoopFunction()  {
		double min = Double.MAX_VALUE;
		@Override Number getInitialAccumulator() {
			//! static class !! reinitialize variables!
			min = Double.MAX_VALUE;
			return 0.0;
		}
		@Override Number handleLoopNumber(final Number accumulator, final Number loopResult) {
			if (loopResult==null)
				return accumulator;
			if (loopResult.doubleValue()<min)
				min = loopResult.doubleValue();
			return loopResult.doubleValue();
		}
		@Override
		Number handleResult(final Number accumulator) {
			return min;
		}
	};
	
	static final Function cmax = new OneArgumentLoopFunction()  {
		double max = Double.MIN_VALUE;
		@Override Number getInitialAccumulator() {
			//! static class !! reinitialize variables!
			max = Double.MIN_VALUE;
			return 0.0;
		}
		@Override Number handleLoopNumber(final Number accumulator, final Number loopResult) {
			if (loopResult==null)
				return accumulator;
			if (loopResult.doubleValue()>max)
				max = loopResult.doubleValue();
			return loopResult.doubleValue();
		}
		@Override
		Number handleResult(final Number accumulator) {
			return max;
		}	
	};
	
		
	final static Map<String,Function> functions = populateFunctions();

	private static Map<String, Function> populateFunctions() {
		final Map<String,Function> functions = new HashMap<String, Function>();
		functions.put("abs", abs);
		functions.put("sqrt", sqrt);
		functions.put("log", log);
		functions.put("ln", ln);
		functions.put("ld", ld);
		functions.put("exp", exp);
		functions.put("min", min);
		functions.put("max", max);
		
		functions.put("csum", csum);
		functions.put("cmean", cmean);
		functions.put("cmin", cmin);
		functions.put("cmax", cmax);
		return functions;
	}
}
