package org.rosuda.util.process;

public class RunStateHolder<T> implements HasRunState<T>{

	private RUNSTATE currentState;
	
	@Override
	public RUNSTATE getRunState() {
		return currentState;
	}

	public void setRunState(final RUNSTATE newState) {
		this.currentState = newState;
	}
}
