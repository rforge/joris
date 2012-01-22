package org.rosuda.graph.service.util;

import java.text.MessageFormat;
import java.util.Map;

abstract class AbstractNumberSQLValueQueryAppender extends AbstractSQLValueQueryAppender<Number>{

	@Override
	protected void handleAppendToSqlQuery(StringBuilder queryStub,
			Map<String, Object> queryArguments, Number value, String parentJoinId) {
		final String valueArg = "val"+queryArguments.size();
		queryArguments.put(valueArg, value);
		queryStub.append(MessageFormat.format(" {0}.num {1} :{2}", parentJoinId, getOperator(), valueArg));
	}
	
	abstract String getOperator();

}
