package org.rosuda.util.java;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

public class ClassPathUtil {

    public static String getLibrariesAsClassPathString() {
	ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
	final String classPathSeparatorChar = System.getProperty("path.separator");
	if (contextClassLoader instanceof URLClassLoader) {
	    final URLClassLoader urlClassLoader = (URLClassLoader) contextClassLoader;
	    final StringBuilder builder = new StringBuilder();
	    boolean requiresSeparator = false;
	    for (URL loadedUrls : urlClassLoader.getURLs()) {
		final String urlString = loadedUrls.toExternalForm();
		if (urlString.toLowerCase().endsWith(".jar")) {
		    if (requiresSeparator) {
			builder.append(classPathSeparatorChar);
		    }
		    builder.append(new File(loadedUrls.getFile()).getAbsolutePath());
		    requiresSeparator = true;
		}
	    }
	    return builder.toString();
	}
	else {
	    return System.getProperty("java.class.path");
	}
    }

}
