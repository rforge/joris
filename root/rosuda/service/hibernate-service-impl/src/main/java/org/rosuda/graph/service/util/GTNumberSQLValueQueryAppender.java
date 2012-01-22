package org.rosuda.graph.service.util;


public class GTNumberSQLValueQueryAppender extends AbstractNumberSQLValueQueryAppender {

	@Override
	String getOperator() {
		return ">";
	}

}
