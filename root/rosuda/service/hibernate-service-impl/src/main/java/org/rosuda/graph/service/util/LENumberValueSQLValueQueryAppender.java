package org.rosuda.graph.service.util;


public class LENumberValueSQLValueQueryAppender  extends AbstractNumberSQLValueQueryAppender {

	@Override
	String getOperator() {
		return "<=";
	}

}
