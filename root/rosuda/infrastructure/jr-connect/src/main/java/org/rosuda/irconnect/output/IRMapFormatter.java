package org.rosuda.irconnect.output;

import java.util.AbstractList;
import java.util.Collection;

import org.rosuda.irconnect.IREXP;
import org.rosuda.irconnect.IRMap;

public class IRMapFormatter extends DelegetableObjectFormatter<IRMap> {

	public IRMapFormatter(final ObjectFormatter objectFormatter) {
		super(objectFormatter);
	}

	@Override
	public String format(final IRMap value) {
		final Collection<IREXP> irMapCollectionWrapper = new AbstractList<IREXP>() {
			@Override
			public IREXP get(int index) {
				return value.at(value.keys()[index]);
			}
			@Override
			public int size() {
				return value.keys().length;
			}
		};
		return objectFormatter.format(irMapCollectionWrapper);
	}

}
