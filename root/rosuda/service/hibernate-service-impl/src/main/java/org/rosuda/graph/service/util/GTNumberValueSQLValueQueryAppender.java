package org.rosuda.graph.service.util;


public class GTNumberValueSQLValueQueryAppender extends AbstractNumberSQLValueQueryAppender {

	@Override
	String getOperator() {
		return ">";
	}

}
