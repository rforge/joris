package org.rosuda.graph.service.util;

import java.util.Map;

public interface SQLValueQueryAppender<TYPE> {

	void appendToSqlQuery(final StringBuilder queryStub, final Map<String, Object> queryArguments, TYPE type, final String parentJoinId);
}
