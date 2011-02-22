package org.rosuda.util.process;

public interface ProcessService<T> extends ProcessStarter<T>, ProcessStopper<T>, HasRunState<T>{
	

}
