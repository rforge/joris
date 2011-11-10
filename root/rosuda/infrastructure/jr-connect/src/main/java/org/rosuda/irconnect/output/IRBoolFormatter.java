package org.rosuda.irconnect.output;

import org.rosuda.irconnect.IRBool;

public class IRBoolFormatter extends AbstractObjectFormatter implements TypedObjectFormatter<IRBool>{

	public final String format(final IRBool bool) {
		if (bool == null) {
			return getReplacement("IRBool.NULL");
		} else if (bool.isNA()) {
			return getReplacement("IRBool.NA");
		} else if (bool.isFALSE()){
			return getReplacement("IRBool.FALSE");
		} else if (bool.isTRUE()) {
			return getReplacement("IRBool.TRUE");
		} else {
			return getReplacement("IRBool.UNDEFINED");
		}
	}
	
}
