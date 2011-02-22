package org.rosuda.util.calculator.parser;


public class Token {
	//for calculation
	public enum Type {
		Operation, Function, Structure, Separator, Number, Reference, End	 	 
	}
	
	final String symbol;
	final Type type;
	final Number value;	
	final int position;

	Token(final String symbol, final Type type, final Number value, final int position) {
		this.symbol = symbol;
		this.type = type;
		this.value = value;	
		this.position = position;
	}
	
	@Override
	public String toString() {
		return new StringBuffer()
		.append("Token(symbol=").append(symbol)
		.append(",type=").append(type)
		.append(",value=").append(value)
		.append(",position=").append(position)
		.append(")").toString();
	}
}
