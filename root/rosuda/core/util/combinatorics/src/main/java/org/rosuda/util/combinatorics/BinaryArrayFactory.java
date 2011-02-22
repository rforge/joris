package org.rosuda.util.combinatorics;

public class BinaryArrayFactory<T> {

	public BinaryArray<T> create(final int n) {
		return new BinaryArrayImpl<T>(n);
	}

	/**
	 * 
	 * @param variables
	 * @param concurrent
	 * @return
	 */
	public BinaryArray<String> create(final int variables, int concurrent) {
		return new SubsetBinaryArray<String>(variables, concurrent);
	}
}
