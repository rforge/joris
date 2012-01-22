package org.rosuda.graph.service.util;

import java.util.Map;

abstract class AbstractSQLValueQueryAppender<TYPE> implements SQLValueQueryAppender<TYPE>{

	@Override
	public final void appendToSqlQuery(StringBuilder whereStub,
			Map<String, Object> queryArguments, TYPE value, String parentJoinId) {
		if (whereStub.length() > 1) {
			whereStub.append(" AND");
		} else {
			whereStub.append(" WHERE");
		}
		handleAppendToSqlQuery(whereStub, queryArguments, value, parentJoinId);
	}

	protected abstract void handleAppendToSqlQuery(StringBuilder queryStub,
			Map<String, Object> queryArguments, TYPE value,
			String parentJoinId);

}
