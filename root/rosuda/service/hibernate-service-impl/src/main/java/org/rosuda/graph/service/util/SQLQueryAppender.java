package org.rosuda.graph.service.util;

import java.util.Map;

import org.rosuda.graph.service.search.VertexConstraint;

public interface SQLQueryAppender<CONSTRAINT extends VertexConstraint> {

	void appendToSqlQuery(final SQLQueryBuilder queryBuilder, final StringBuilder queryStub, final StringBuilder whereQueryStub, final Map<String, Object> queryArguments, CONSTRAINT constraint, final String parentJoinId) throws InstantiationException, IllegalAccessException, ClassNotFoundException;
}
