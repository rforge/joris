package org.rosuda.graph.service.util;

import java.text.MessageFormat;
import java.util.Map;

import org.rosuda.graph.service.search.NameVertexConstraint;

public class NameVertexSQLQueryAppender implements SQLQueryAppender<NameVertexConstraint>{

	@Override
	public void appendToSqlQuery(final SQLQueryBuilder queryBuilder, final StringBuilder queryStub,  final StringBuilder whereQueryStub,
			final Map<String, Object> queryArguments,
			final NameVertexConstraint constraint, final String parentJoinId) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		final String queryAlias = "vertex"+queryArguments.size();
		final String argName = "name"+queryArguments.size();
		queryStub.append(MessageFormat.format(" JOIN VERTEX {0} ON ({0}.gra_id = graph.gra_id AND {0}.name = :{1})", queryAlias, argName));
		if (parentJoinId != null) {
			final String edgeAlias = "edge"+queryArguments.size();
			queryStub.append(MessageFormat.format(" JOIN EDGE {0} ON ({0}.from_id = {1}.ver_id AND {0}.to_id = {2}.ver_id)", edgeAlias, parentJoinId, queryAlias));
		}
		queryArguments.put(argName, constraint.getName());
		queryBuilder.appendValueConstraintsToQueryStub(whereQueryStub, constraint.getValueConstraints(), queryArguments, queryAlias);
		queryBuilder.appendConstraintsToQueryStub(queryStub, whereQueryStub, constraint.getChildConstraints(), queryArguments, queryAlias);
	}

}
