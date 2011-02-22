package org.rosuda.util.combinatorics;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import org.junit.Assert;
import org.junit.Test;

/**
 * this is kind of a debug test
 * @author ralfseger
 *
 */
public class BinaryArrayTest {

	private boolean debug = false;
	
	private static final String makeString(final String[] vars) {
		final StringBuilder builder = new StringBuilder();
		for (int i=0;i<vars.length;i++) {
			if (i>0)
				builder.append(" + ");
			builder.append(vars[i]);
		}
		return builder.toString();
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
	
	private int testFor(final String[] variables) {
		int invalidCount = 0;
		int validCount = 0;
		int iter = 0;
		BinaryArray<String> loop = new BinaryArrayImpl<String>(variables.length);
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
			if (debug) System.out.println("iter "+(iter)+":"+makeString(matches));
		}
		if (debug) System.out.println("valid "+validCount+" invalid "+invalidCount + 
				" total = "+(validCount+invalidCount) +  
				" pct = "+ ( (double) validCount) /  ( (double) (validCount+invalidCount)));
		return iter;
	}

	@Test
	public void testBinaryArrayImpl(){
		Assert.assertEquals(8, testFor(new String[]{"a","b","a:b"}));
		Assert.assertEquals(64, testFor(new String[]{"a","b","c","a:b","a:c","b:c"}));
		Assert.assertEquals(1024, testFor(new String[]{"a","b","c","d","a:b","a:c","a:d","b:c","b:d","c:d"}));
		//Assert.assertEquals(2^21, testFor(new String[]{"a","b","c","d","e","f","a:b","a:c","a:d","a:e","a:f","b:c","b:d","b:e","b:f","c:d","c:e","c:f","d:e","d:f","e:f"}));
		//Assert.assertEquals(2^28, testFor(new String[]{"a","b","c","d","e","f","g","a:b","a:c","a:d","a:e","a:f","a:g","b:c","b:d","b:e","b:f","b;g","c:d","c:e","c:f","c:g","d:e","d:f","d:g","e:f","e:g","f:g"}));
	}


}
