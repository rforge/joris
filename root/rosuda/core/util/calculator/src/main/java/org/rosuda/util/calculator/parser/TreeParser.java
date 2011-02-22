package org.rosuda.util.calculator.parser;

import java.util.Iterator;
import java.util.Stack;

import org.rosuda.util.calculator.parser.TreeNode.Type;



public class TreeParser {

	public class TreeParserException extends RuntimeException {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 3490174800044689779L;
		
		private final Token token;
		
		private TreeParserException(final String string, final Token currentToken) {
			super(string);
			this.token = currentToken;
		}
		
		@Override
		public String getMessage() {
			return new StringBuffer().append(super.getMessage()).append(", @Token=").append(token).toString();
		}
	}	
	
	
	public TreeParser() {
	}

	
	public TreeNode parse(final Tokenizer tokenizer) throws TreeParserException{
		//initialize recursion:
		final Stack<TreeNode> stack = new Stack<TreeNode>();
		TreeNode root = parse(stack, tokenizer.getTokenIterator());
		return root;
	}
	
	//sideways parse
	private TreeNode parse( final Stack<TreeNode> stack,	
							final Iterator<Token> tokenIterator) throws TreeParserException{
		final Token currentToken = tokenIterator.next();
		switch (currentToken.type) {
			case End:
				if (!stack.isEmpty())
					return stack.pop();//should alway contain node - else parse error!
				return null;
			case Reference: case Number:
				stack.add(buildLeaf(currentToken));
				break;	
			case Operation:
				return buildOperationNode(currentToken, stack, tokenIterator);
			case Structure:
				if ("(".equals(currentToken.symbol)) {
					stack.add(startBracket(currentToken, stack, tokenIterator));
					break;
				} else if (")".equals(currentToken.symbol)){
					return endBracket(currentToken, stack, tokenIterator);		
				}
				break;
			case Function:
				stack.add(buildFunctionNode(currentToken, stack, tokenIterator));
				break;
			case Separator:
				return processSeparator(currentToken, stack, tokenIterator);		
		}
		if (!tokenIterator.hasNext()) {
			return stack.pop();
		}
		return parse(stack, tokenIterator);	
	}
	
	//deep parse
	private TreeNode makeInternalStructure( final Stack<TreeNode> stack,	
			final Iterator<Token> tokenIterator) throws TreeParserException{
		final Token currentToken = tokenIterator.next();
		switch (currentToken.type) {
			case End:
				if (!stack.isEmpty())
					return stack.pop();//should alway contain node - else parse error!
				return null;
			case Reference: case Number:
				return buildLeaf(currentToken);		
			case Operation:
				return buildOperationNode(currentToken, stack, tokenIterator);
			case Structure:
				if ("(".equals(currentToken.symbol)) {
					//add internally to stack
					return startBracket(currentToken, stack, tokenIterator);
				} else if (")".equals(currentToken.symbol)){
					return endBracket(currentToken, stack, tokenIterator);		
				}
				break;
			case Function:
				return buildFunctionNode(currentToken, stack, tokenIterator);
			case Separator:
				return processSeparator(currentToken, stack, tokenIterator);		
		}
		throw new IllegalArgumentException("unknotn token type "+currentToken.type);
	}
	
	private Type operationToType(final Token currentToken) {
		Type type = null;
		if ("+".equals(currentToken.symbol)) {
			type = Type.ADD;
		} else if ("-".equals(currentToken.symbol)) {
			type = Type.SUB;
		} else if ("*".equals(currentToken.symbol)) {
			type = Type.MUL;
		} else if ("/".equals(currentToken.symbol)) {
			type = Type.DIV;
		} else if ("^".equals(currentToken.symbol)) {
			type = Type.POW;
		} else {
			throw new UnsupportedOperationException("unsupported symbol "+currentToken.symbol);
		}
		return type;
	}	
	
	private TreeNode buildLeaf(final Token currentToken) {
		return TreeNode.buildLeaf(currentToken);
	}
	
	private TreeNode buildFunctionNode(final Token currentToken, final Stack<TreeNode> stack, final Iterator<Token> tokenIterator) {
		final TreeNode function = TreeNode.create(Type.FUNCTION, currentToken.symbol);	
		//function needs inner argument (in brackets!)
		final TreeNode innerFunctionNode = makeInternalStructure(stack, tokenIterator);
		if (!Type.BRACKET.equals(innerFunctionNode.getType()))
			throw new IllegalArgumentException("cannot compute function {"+currentToken+"} without bracket argument!");
		function.addChild(innerFunctionNode);
		return function;
	}
	
	private TreeNode startBracket(final Token currentToken, final Stack<TreeNode> stack, final Iterator<Token> tokenIterator) {
		final TreeNode bracket = TreeNode.buildStructure(TreeNode.Type.BRACKET);
		//bracket needs inner argument (or by separator "," multiple)
		final TreeNode argument = parse(stack, tokenIterator);
		bracket.addChild(argument);
		return bracket;
	}
	
	private TreeNode endBracket(final Token currentToken, final Stack<TreeNode> stack, final Iterator<Token> tokenIterator) {
		return stack.pop();
	}
	
	private TreeNode buildOperationNode(final Token currentToken, final Stack<TreeNode> stack, final Iterator<Token> tokenIterator) {
		final Type currentTokenType = operationToType(currentToken);
		TreeNode operation = TreeNode.buildStructure(currentTokenType);
		if (stack.isEmpty())
			throw new IllegalArgumentException("unsupported operation on empty stack");
		//functions can only be found on stack at this point and not as bracket
		operation.addChild(stack.pop());	
		//operator needs at least a second argument
		final TreeNode argument = parse(stack, tokenIterator);
		//another operation of same type encountered => pull up children
		if (argument.getType().equals(currentTokenType)) {
			for (final TreeNode grandChild : argument.getChildren()) {
				operation.addChild(grandChild);
			}
		} else /*check math rules *,/ > +,- */ if (TreeNode.typeComparator.compare(argument.getType(), currentTokenType)>0){
			final TreeNode oldFirst = argument.getChildren().get(0);
			argument.removeChild(oldFirst);
			operation.addChild(oldFirst);
			argument.children.add(0,operation);
			return argument;
		} else {
			operation.addChild(argument);
		}
		return operation;
	}
	
	private TreeNode processSeparator(final Token currentToken, final Stack<TreeNode> stack, final Iterator<Token> tokenIterator) {
		TreeNode separator = TreeNode.buildStructure(TreeNode.Type.SEPARATOR);
		if (stack.size()>0) {
			separator.addChild(stack.pop());
		} 
		//operator needs at least a second argument
		final TreeNode argument = parse(stack, tokenIterator);
		//another operation of same type encountered => pull up children
		if (TreeNode.Type.SEPARATOR.equals(argument.getType())) {
			for (final TreeNode grandChild : argument.getChildren()) {
				separator.addChild(grandChild);
			}
		} else {
			separator.addChild(argument);
		}
		return separator;
	}
}
