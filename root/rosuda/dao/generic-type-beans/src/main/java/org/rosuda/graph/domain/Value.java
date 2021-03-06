package org.rosuda.graph.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Embeddable
public class Value {

    private static final int NUMBER_SCALE = 8;
    private static final int NUMBER_PRECISIION = 20;
    public enum Type {
	NUMBER, STRING, BOOL, REFERENCE
    }

    @Column(name = "NUM", scale = NUMBER_SCALE, precision = NUMBER_PRECISIION)
    private BigDecimal number;
    @Column(name = "STRING")
    private String string;
    @Column(name = "REF")
    private String reference;
    @Column(name = "BOOL")
    private Boolean bool;
    @Column(name = "TYPE")
    @Enumerated(EnumType.ORDINAL)
    private Type type;

    public BigDecimal getNumber() {
	return number;
    }

    public void setNumber(final BigDecimal number) {
	this.number = number.setScale(NUMBER_SCALE + NUMBER_PRECISIION, BigDecimal.ROUND_HALF_DOWN);
	
	this.type = Type.NUMBER;
    }

    public void setNumber(final Number number) {
	if (number != null) {
	    if ((double) number.longValue() == number.doubleValue()) {
		final BigDecimal bigDValue = new BigDecimal(number.longValue());
		setNumber(bigDValue);
	    } else {
		final BigDecimal bigDValue = new BigDecimal(number.doubleValue());
		setNumber(bigDValue);
	    }
	}
    }

    public String getString() {
	return string;
    }

    public void setString(final String string) {
	this.string = string;
	this.type = Type.STRING;
    }

    public void setReference(final String refId) {
	this.type = Type.REFERENCE;
	this.reference = refId;
    }

    public Boolean getBool() {
	return bool;
    }

    public void setBool(final Boolean bool) {
	this.bool = bool;
	this.type = Type.BOOL;
    }

    public String getReference() {
	return this.reference;
    }

    public Type getType() {
	return type;
    }

    public void setType(final Type type) {
	this.type = type;
    }

    @Override
    public String toString() {
	if (type == null)
	    return "NVAL";
	switch (type) {
	case BOOL:
	    return this.bool.toString();
	case NUMBER:
	    return this.number.toString();
	case STRING:
	    return this.string;
	case REFERENCE:
	    return this.reference;
	}
	return super.toString();
    }
}
