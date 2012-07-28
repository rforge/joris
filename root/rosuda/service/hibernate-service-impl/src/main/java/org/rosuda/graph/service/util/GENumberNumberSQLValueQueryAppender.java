package org.rosuda.graph.service.util;


public class GENumberNumberSQLValueQueryAppender  extends AbstractNumberSQLValueQueryAppender {

	@Override
	String getOperator() {
		return ">=";
	}

}
