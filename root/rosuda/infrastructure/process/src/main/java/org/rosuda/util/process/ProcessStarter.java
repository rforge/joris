package org.rosuda.util.process;

/**
 * start a process that ensures the existence of a <T> 
 * T could be a java.sql.Connection, 
 * @author ralfseger
 *
 * @param <T>
 */
public interface ProcessStarter<T> {

	void start();
	
}
