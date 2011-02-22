package org.rosuda.util.calculator.parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.rosuda.util.calculator.parser.Token.Type;

public class Tokenizer {
	
	private class PreToken {
		final Token.Type type;
		final int start;
		final int end;
		final Number value;
		final String functionOrReferenceName;
		
		PreToken(final Token.Type type,final Number value,final String functionOrReferenceName,final int start, final int end) {
			this.type = type;
			this.start = start;
			this.end = end;
			this.value = value;
			this.functionOrReferenceName = functionOrReferenceName;
		}
	}
	
	private List<Token> tokens = new ArrayList<Token>();
	
	public Tokenizer(final String source) {
		final String cleanSource = escape(source);
		final List<PreToken> preTokens = split(cleanSource);
		compile(preTokens);
	}

	// filter tabs,space and crt
	private String escape(final String text) {
		final StringTokenizer tokenizer = new StringTokenizer(text, " \n\r\t",
				false);
		final StringBuffer buffer = new StringBuffer();
		while (tokenizer.hasMoreElements()) {
			buffer.append(tokenizer.nextToken());
		}
		buffer.append("\n");
		return text;
	}

	// filter out numbers
	private List<PreToken> split(final String source) {
		final List<PreToken> preTokens= new ArrayList<PreToken>();
		
		for (int i = 0; i < source.length(); i++) {
			final char c = source.charAt(i);
			if (c >= '0' && c <= '9') {
				final PreToken pre = number(source, i);
				i = pre.end - 1;
				preTokens.add(pre);
			} else if (c == '$'){
				/*${model.x} syntax*/
				final PreToken pre = reference(source,i);
				i = pre.end - 1;
				preTokens.add(pre);	
			} else if (c >= 'a' && c<= 'z') {
				final PreToken pre = function(source, i);
				i = pre.end - 1;
				preTokens.add(pre);
		    } else if (c == '+' || c == '-' || c == '*' || c== '/' || c == '^') {
				preTokens.add(new PreToken(Type.Operation, null, ""+c, i, i+1));
			} else if (c == '(' || c == ')') {
				preTokens.add(new PreToken(Type.Structure, null, ""+c, i, i+1));
			} else if (c == ',') {
				preTokens.add(new PreToken(Type.Separator, null, ",", i, i+1));
			} 
			else {
				throw new IllegalArgumentException("string \""+source+"\" contains unrecognized character '"+c+"'");
			}
		}
		return preTokens;
	}

	private void compile(final List<PreToken> preTokens) {
		boolean invert = false;
		for (final PreToken pre: preTokens) {
			//perpare -x numbers
			//(- or void-
			final Token currentToken = new Token(pre.functionOrReferenceName, pre.type, pre.value, pre.start);
			if (currentToken.type.equals(Token.Type.Operation)) {
				if ("-".equals(currentToken.symbol)) {
					if (tokens.isEmpty() || 
						tokens.size() > 0 && tokens.get(tokens.size() - 1).type.equals(Token.Type.Structure) && "(".equals(tokens.get(tokens.size() - 1).symbol) ){
						invert = true;
						continue;
					} 
				} 
				tokens.add(currentToken);
			} else if (currentToken.type.equals(Token.Type.Number) && invert){
				tokens.add(new Token(currentToken.symbol, currentToken.type, -1 * currentToken.value.doubleValue(), currentToken.position));
				invert = false;
			} else {
				tokens.add(currentToken);
			}	
		}
		tokens.add(new Token(null, Token.Type.End, null,Integer.MAX_VALUE));
	}
	
	//helpers
	private PreToken reference(final String source, final int i) {
		final int tokenStart = source.indexOf("{",i);
		final int tokenEnd = source.indexOf("}",tokenStart);
		return new PreToken(Type.Reference, null, source.substring(tokenStart+1, tokenEnd), i, tokenEnd+1); 
	}
	
	private PreToken number(final String source, final int i) {
		int j = i, n = source.length();
		Number num = null;
		while (j < n && Character.isDigit(source.charAt(j)))
			j++;
		if (j < n && source.charAt(j) == '.') {
			j++;
			if (j < n && Character.isDigit(source.charAt(j))) {
				while (j < n && Character.isDigit(source.charAt(j)))
					j++;
				num = Double.parseDouble(source.substring(i,j));
			} 
		} else
			num = Double.parseDouble(source.substring(i,j));
		return new PreToken(Type.Number, num, null, i, j);
	}
	
	private PreToken function(final String source, final int i) {
		//try to find function word from source ..
		final int end = source.indexOf("(",i);
		return new PreToken(Type.Function, null, source.substring(i, end), i, end);
	}

	Token[] getTokens() {
		return tokens.toArray(new Token[0]);
	}
	
	Iterator<Token> getTokenIterator() {
		return tokens.iterator();
	}
}
