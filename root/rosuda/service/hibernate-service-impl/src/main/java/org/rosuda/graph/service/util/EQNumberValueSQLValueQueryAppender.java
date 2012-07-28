package org.rosuda.graph.service.util;


public class EQNumberValueSQLValueQueryAppender  extends AbstractNumberSQLValueQueryAppender {

	@Override
	String getOperator() {
		return "=";
	}

}