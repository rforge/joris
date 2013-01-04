package org.rosuda.graph.service.util;


public class GENumberValueSQLValueQueryAppender  extends AbstractNumberSQLValueQueryAppender {

	@Override
	String getOperator() {
		return ">=";
	}

}
