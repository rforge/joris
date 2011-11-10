package org.rosuda.irconnect.output;

import java.util.AbstractList;
import java.util.Collection;

import org.rosuda.irconnect.IREXP;
import org.rosuda.irconnect.IRList;

public class IRListFormatter extends DelegetableObjectFormatter<IRList>{

	public IRListFormatter(final ObjectFormatter objectFormatter) {
		super(objectFormatter);
	}

	@Override
	public String format(final IRList value) {
		final Collection<IREXP> iRListCollectionWrapper = new AbstractList<IREXP>() {
			@Override
			public IREXP get(int index) {
				return value.at(value.keys()[index]);
			}
			@Override
			public int size() {
				return value.keys().length;
			}
		};
		return objectFormatter.format(iRListCollectionWrapper);
	}


}
