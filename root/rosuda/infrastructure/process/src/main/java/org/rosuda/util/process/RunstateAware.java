package org.rosuda.util.process;

public class RunstateAware<T> {

	protected final RunStateHolder<T> runStateHolder;
	
	protected RunstateAware(final RunStateHolder<T> runStateHolder) {
		this.runStateHolder = runStateHolder;
	}
}
