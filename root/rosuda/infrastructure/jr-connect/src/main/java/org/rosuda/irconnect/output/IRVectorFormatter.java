package org.rosuda.irconnect.output;

import java.util.AbstractList;
import java.util.Collection;

import org.rosuda.irconnect.IREXP;
import org.rosuda.irconnect.IRVector;

public class IRVectorFormatter extends DelegetableObjectFormatter<IRVector>{

	public IRVectorFormatter(final ObjectFormatter objectFormatter) {
		super(objectFormatter);
	}

	@Override
	public String format(final IRVector value) {
		final Collection<IREXP> iRListCollectionWrapper = new AbstractList<IREXP>() {
			@Override
			public IREXP get(int index) {
				return value.at(index);
			}
			@Override
			public int size() {
				return value.size();
			}
		};
		return objectFormatter.format(iRListCollectionWrapper);
	}

}
