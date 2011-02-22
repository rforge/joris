/*
 * Created on 20.08.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.rosuda.util.combinatorics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * @author araseg
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
class SubsetBinaryArray<T> implements BinaryArray<T>{
		
	private boolean matchedLast = false;
	private boolean reverseMatchMode = false;
	private final short[] array;
	private final short[] max;
	/**
	 * # of bits that can be set concurrently
	 */
	private final int maxBits;
	
	SubsetBinaryArray(final int nbits,final int concurrentMax) {
		if (nbits<1||concurrentMax<1)
			throw new IllegalArgumentException("cannot create BinaryArray with negative Argument!");
		array=new short[nbits];
		max=new short[nbits];
		for (int i=0;i<nbits;i++) {
			array[i]=0;
			max[i]=(short) ((i<=concurrentMax)?1:0);
		}
		maxBits=concurrentMax;
//System.out.println("maxBits"+maxBits);		
	}
	
	
	public boolean hasNext()
	{
		int matchCount = 0;
		for (int i=0;i<array.length;i++)
			if (array[i]==max[i])
				matchCount ++;
		if (matchCount < array.length) {
			return true;
		} else if (matchedLast){ 
			return false;
		} else {
			matchedLast = true;
			return true;
		}
	}
		
	public BinaryArray<T> next() {
		if (matchedLast)
			return this;
		for (int i=array.length-1;i>=0 ;i--) {
			if (array[i]==0) {
				array[i] = 1;
				i = -1;
			} else {
				array[i] = 0;
			}
		}
		int concurrent = 0;
		for (int i=0;i<array.length;i++) {
			concurrent += array[i];
		}
		if (concurrent > maxBits && hasNext()) {
//System.out.println("skip ("+concurrent+">"+maxBits+")"+this);
			return next();
		} else {
			return this;
		}
	}
		
	private ArrayList<T> match(final T[] objects) {
		final ArrayList<T> list = new ArrayList<T>();
		final int length=Math.min(objects.length,array.length);
		for (int i=0;i<length;i++) {
			final boolean bitSet = array[i] == 1;
			if (!reverseMatchMode&&bitSet||reverseMatchMode&&!bitSet) {
				list.add(objects[i]);
			}			
		}
		return list;
	}
	
	public Iterator<T> matchArray(final T[] objects) {
		return match(objects).iterator();	
	}
	
	@SuppressWarnings("unchecked")
	public T[] matchSubset(final T[] objects)
	{
		final List<T> match = match(objects);
		return match.toArray((T[])new Object[match.size()]);
	}
	
	public String toString()
	{
		StringBuffer buffer=new StringBuffer();
		for (int i=0;i<array.length;i++) {
			buffer.append(array[i]);
		}
		return buffer.toString();		
	}
	
	/* (non-Javadoc)
	 * @see java.util.Iterator#remove()
	 */
	public void remove() {
		next();
	}


	/* (non-Javadoc)
	 * @see de.augsburg.uni.rosuda.gui.util.IBinaryArray#getCombinationCount()
	 */
	public double getCombinationCount() {
		// TODO Auto-generated method stub
		double combinations=0;
		for (int i=1;i<=maxBits;i++)
		{						
			combinations+=choose(array.length,i);			
		}		

		return combinations;
	}
	
	private double choose(final int n, final int k)
	{
		return factorial(n)/factorial(k)/factorial(n-k);
	}
	
	private double factorial(final int n)
	{
		double val=1;
		for (int i=2;i<=n;i++)
		{
			val*=(double)i;
		}
		return val;
	}


	public void setReverseMatchMode(final boolean reverseMatching) {
		this.reverseMatchMode = reverseMatching;
	}


	public void initializeStartBits(final long value) {
		final String binaryInit = Long.toBinaryString(value);
		int binaryIdx = binaryInit.length()-1;
		for (int i=maxBits-1;i>=0;i--) {
			if (binaryIdx>-1) {
				if (binaryInit.charAt(binaryIdx)=='0')
					array[i] = 0;
				else if (binaryInit.charAt(binaryIdx)=='1')
					array[i] = 1;
			}
			binaryIdx--;		
		}
		
	}
	
	private static boolean isValidSet(final String[] subsetVariables) {
		final Set<String> basicParameters = new HashSet<String>();
		final Set<String> interactions = new HashSet<String>();
		for (int i = 0; i < subsetVariables.length; i++) {
			final String parameter = subsetVariables[i];
			if (parameter.indexOf(":")>-1)
				interactions.add(parameter);
			else
				basicParameters.add(parameter);
		}
		for (Iterator<String> iter = interactions.iterator(); iter.hasNext();) {
			final String interaction = (String) iter.next();
			final StringTokenizer basicParametersTokenizer = new StringTokenizer(interaction, ":", false);
			while (basicParametersTokenizer.hasMoreTokens())
			{
				final String token = basicParametersTokenizer.nextToken();
				if (!basicParameters.contains(token))
					return false;
			}			
		}
		return true;
	}

	private static final String makeString(final String[] vars) {
		final StringBuilder builder = new StringBuilder();
		for (int i=0;i<vars.length;i++) {
			if (i>0)
				builder.append(" + ");
			builder.append(vars[i]);
		}
		return builder.toString();
	}
	
	public static final void main(final String[] args) {
		final String[] variables = 
			//new String[]{"a","b","a:b"};
			new String[]{"a","b","c","a:b","a:c","b:c"};
			//new String[]{"a","b","c","d","a:b","a:c","a:d","b:c","b:d","c:d"};
			//new String[]{"a","b","c","d","e","a:b","a:c","a:d","a:e","b:c","b:d","b:e","c:d","c:e","d:e"};
			//new String[]{"a","b","c","d","e","f","a:b","a:c","a:d","a:e","a:f","b:c","b:d","b:e","b:f","c:d","c:e","c:f","d:e","d:f","e:f"};
			//new String[]{"a","b","c","d","e","f","g","a:b","a:c","a:d","a:e","a:f","a:g","b:c","b:d","b:e","b:f","b;g","c:d","c:e","c:f","c:g","d:e","d:f","d:g","e:f","e:g","f:g"};
		
		
		BinaryArray<String> loop = new SubsetBinaryArray<String>(variables.length,variables.length);
		int invalidCount = 0;
		int validCount = 0;
		int iter = 0;
		while (loop.hasNext()) {
			iter ++; loop.next();
			final Object[] matchedObjects = loop.matchSubset(variables);
			String[] matches = new String[matchedObjects.length];
			for (int i=0;i<matches.length;i++) {
				matches[i] = (String) matchedObjects[i];
			}
			if (isValidSet(matches)) {
				validCount ++;
			} else {
				invalidCount ++;
			}
			System.out.println("iter "+(iter)+":"+makeString(matches));
		}
		System.out.println("valid "+validCount+" invalid "+invalidCount + 
				" total = "+(validCount+invalidCount) +  
				" pct = "+ ( (double) validCount) /  ( (double) (validCount+invalidCount)));
	}
}
