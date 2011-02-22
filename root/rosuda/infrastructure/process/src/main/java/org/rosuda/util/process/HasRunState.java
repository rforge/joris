package org.rosuda.util.process;

/**
 * information about the current status of the service
 * @author ralfseger
 *
 * @param <T>
 */
public interface HasRunState<T> {

	RUNSTATE getRunState();

}
