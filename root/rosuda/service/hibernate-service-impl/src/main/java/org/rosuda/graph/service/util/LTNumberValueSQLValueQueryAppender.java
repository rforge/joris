package org.rosuda.graph.service.util;


public class LTNumberValueSQLValueQueryAppender extends AbstractNumberSQLValueQueryAppender {

	@Override
	String getOperator() {
		return "<";
	}

}