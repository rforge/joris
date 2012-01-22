package org.rosuda.graph.service.util;


public class EQNumberSQLValueQueryAppender  extends AbstractNumberSQLValueQueryAppender {

	@Override
	String getOperator() {
		return "=";
	}

}