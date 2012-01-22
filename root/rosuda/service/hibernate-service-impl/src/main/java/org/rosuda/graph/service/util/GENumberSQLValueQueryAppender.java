package org.rosuda.graph.service.util;


public class GENumberSQLValueQueryAppender  extends AbstractNumberSQLValueQueryAppender {

	@Override
	String getOperator() {
		return ">=";
	}

}
