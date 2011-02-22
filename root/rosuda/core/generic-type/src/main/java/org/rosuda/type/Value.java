package org.rosuda.type;

import java.io.Serializable;

public final class Value implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3695679422241680386L;

	public enum Type {
		NUMBER, STRING, BOOL, REFERENCE
	}
	
	private final Number number;
	private final String string;
	private final Boolean bool;
	private final Type type;
	
	private Value(final Number number, final String string, final Boolean bool, final Type type) {
		this.number = number;
		this.string = string;
		this.bool = bool;
		this.type = type;
	}
	
	public static Value newNumber(final Number number) {
		return new Value(number, null, null, Type.NUMBER);
	}
	
	public static Value newString(final String string) {
		return new Value(null, string, null, Type.STRING);
	}
	
	public static Value newReference(final String nodeId) {
		return new Value(null, nodeId, null, Type.REFERENCE);
	}
	
	public static Value newBool(final Boolean bool) {
		return new Value(null, null, bool, Type.BOOL);
	}

	public final boolean getBool() {
		if (this.type != Type.BOOL)
			throw new UnsupportedOperationException("cannot cast "+this.type.name()+" to boolean.");
		return bool.booleanValue();
	}

	public final Number getNumber() {
		if (this.type != Type.NUMBER)
			throw new UnsupportedOperationException("cannot cast "+this.type.name()+" to Number.");
		return number;
	}

	public final String getString() {
		if (this.type != Type.STRING)
			throw new UnsupportedOperationException("cannot cast "+this.type.name()+" to String.");
		return string;
	}

	public final String getReference() {
		if (this.type != Type.REFERENCE)
			throw new UnsupportedOperationException("cannot cast "+this.type.name()+" to Reference.");
		return string;
	}

	public Type getType() {
		return type;
	}
	
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder().append("Value[type=").append(type.name()).append(",value=\"");
		switch (type) {
		case BOOL: builder.append(bool); break;
		case NUMBER: builder.append(number); break;
		case STRING: builder.append(string); break;
		case REFERENCE: builder.append("@").append(string); break;
		}
		return builder.append("\"]").toString();
	}
}
