/*
 * Created on 22.08.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.rosuda.util.combinatorics;

import java.util.Iterator;


/**
 * @author araseg
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface BinaryArray<T> extends Iterator<BinaryArray<T>>{
	
	/**
	 * create matching subset from Object[] with 
	 * length of BinaryArray
	 * 
	 * the binaray array consists of an array[true,false]^n
	 * the matching returns all objects where the array is TRUE
	 * 
	 * @param objects
	 * @return
	 */
	public Iterator<T> matchArray(T[] objects);

	/**
	 * create matching subset from Object[] with 
	 * length of BinaryArray
	 * 
	 * the binaray array consists of an array[true,false]^n
	 * the matching returns all objects where the array is TRUE
	 * 
	 * @param objects
	 * @return
	 */	
	public T[] matchSubset(T[] objects);
	
	/**	 
	 * @return the number of combintions for that constructor!
	 */
	public double getCombinationCount();
	
	/**
	 * set to true to invert matching thus starting at 111111 and not 000000
	 * @param reverseMatching
	 */
	public void setReverseMatchMode(final boolean reverseMatching);
	
	/**
	 * initializes the array to a start variable like 13 = 01101
	 * @param value
	 */
	public void initializeStartBits(final long value);
}
