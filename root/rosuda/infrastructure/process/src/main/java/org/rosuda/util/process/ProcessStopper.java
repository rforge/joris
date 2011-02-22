package org.rosuda.util.process;

/**
 * stops a process that provided <T> see {@link ProcessStarter}
 * @author ralfseger
 *
 * @param <T>
 */
public interface ProcessStopper<T> {

	void stop();

}
