package org.rosuda.ui.context;

public interface Aware<T> {

    void setContext(final T context);
    
}
