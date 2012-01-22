package org.rosuda.graph.service.util;


public class LENumberSQLValueQueryAppender  extends AbstractNumberSQLValueQueryAppender {

	@Override
	String getOperator() {
		return "<=";
	}

}
