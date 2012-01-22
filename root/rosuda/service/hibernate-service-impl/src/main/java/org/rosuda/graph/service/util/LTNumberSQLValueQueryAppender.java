package org.rosuda.graph.service.util;


public class LTNumberSQLValueQueryAppender extends AbstractNumberSQLValueQueryAppender {

	@Override
	String getOperator() {
		return "<";
	}

}