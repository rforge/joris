/*
 * Created on 20.08.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.rosuda.util.combinatorics;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author araseg
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
class BinaryArrayImpl<T> implements BinaryArray<T> {

	private boolean matchedLast = false;
	private boolean reverseMatchMode = false;
	private final short[] array;
	private final short[] max;

	BinaryArrayImpl(final int nbits) {
		if (nbits < 1)
			throw new IllegalArgumentException(
					"cannot create BinaryArray with negative Argument!");
		array = new short[nbits];
		max = new short[nbits];
		for (int i = 0; i < nbits; i++) {
			array[i] = 0;
			max[i] = 1;
		}
	}

	public boolean hasNext() {
		int matchCount = 0;
		for (int i = 0; i < array.length; i++)
			if (array[i] == max[i])
				matchCount++;
		if (matchCount < array.length) {
			return true;
		} else if (matchedLast) {
			return false;
		} else {
			matchedLast = true;
			return true;
		}
	}

	public BinaryArray<T> next() {
		if (matchedLast)
			return this;
		for (int i = array.length - 1; i >= 0; i--) {
			if (array[i] == 0) {
				array[i] = 1;
				i = -1;
			} else {
				array[i] = 0;
			}
		}
		return this;
	}

	private ArrayList<T> match(final T[] objects) {
		final ArrayList<T> list = new ArrayList<T>();
		final int length = Math.min(objects.length, array.length);
		for (int i = 0; i < length; i++) {
			final boolean bitSet = array[i] == 1;
			if (!reverseMatchMode && bitSet || reverseMatchMode && !bitSet) {
				list.add(objects[i]);
			}
		}
		return list;
	}

	public Iterator<T> matchArray(final T[] objects) {
		return match(objects).iterator();
	}

	@SuppressWarnings("unchecked")
	public T[] matchSubset(final T[] objects) {
		final List<T> match = match(objects);
		return match.toArray((T[]) new Object[match.size()]);
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < array.length; i++) {
			buffer.append(array[i]);
		}
		return buffer.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#remove()
	 */
	public void remove() {
		next();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.augsburg.uni.rosuda.gui.util.IBinaryArray#getCombinationCount()
	 */
	public double getCombinationCount() {
		// TODO Auto-generated method stub
		double combinations = 0;
		for (int i = 1; i <= array.length; i++) {
			combinations += choose(array.length, i);
		}

		return combinations;
	}

	private double choose(final int n, final int k) {
		return factorial(n) / factorial(k) / factorial(n - k);
	}

	private double factorial(final int n) {
		double val = 1;
		for (int i = 2; i <= n; i++) {
			val *= (double) i;
		}
		return val;
	}

	public void setReverseMatchMode(final boolean reverseMatching) {
		this.reverseMatchMode = reverseMatching;
	}

	public void initializeStartBits(final long value) {
		final String binaryInit = Long.toBinaryString(value);
		int binaryIdx = binaryInit.length() - 1;
		for (int i = array.length - 1; i >= 0; i--) {
			if (binaryIdx > -1) {
				if (binaryInit.charAt(binaryIdx) == '0')
					array[i] = 0;
				else if (binaryInit.charAt(binaryIdx) == '1')
					array[i] = 1;
			}
			binaryIdx--;
		}

	}

}
