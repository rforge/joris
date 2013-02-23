package org.rosuda.util.java;

import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

import org.hamcrest.Matcher;
import org.junit.Test;

public class ClassPathUtilTest {
    
    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void dependantClassesAreFoundInTheClassPath() {
	final Matcher containsHamcrestLibrary = containsString("hamcrest");
	final Matcher containsJUnitLibrary = containsString("junit");
	final Matcher containsExpectedLibraries = both(containsHamcrestLibrary).and(containsJUnitLibrary);
	assertThat(ClassPathUtil.getLibrariesAsClassPathString(), containsExpectedLibraries);
    }
}
