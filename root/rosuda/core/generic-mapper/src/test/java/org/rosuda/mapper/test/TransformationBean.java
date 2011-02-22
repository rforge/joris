package org.rosuda.mapper.test;

public class TransformationBean<T> {

	private T attribute;

	public TransformationBean(final T t) {
		this.attribute = t;
	}

	public void set(final T t) {
		this.attribute = t;
	}
	
	public T get() {
		return attribute;
	}
}
