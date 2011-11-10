package org.rosuda.irconnect.output;

import java.util.ArrayList;
import java.util.List;

import org.rosuda.irconnect.IRFactor;

public class IRFactorFormatter extends DelegetableObjectFormatter<IRFactor>{

	public IRFactorFormatter(final ObjectFormatter objectFormatter) {
		super(objectFormatter);
	}

	@Override
	public String format(final IRFactor value) {
		final List<String> factors = new ArrayList<String>();
		for (int i=0;i<value.size();i++) {
			factors.add(value.at(i));
		}
		return objectFormatter.format(factors);
	}

}
