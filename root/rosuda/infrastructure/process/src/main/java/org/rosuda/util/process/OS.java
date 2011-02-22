package org.rosuda.util.process;

/**
 * utility class to determine the current operating system
 * @author ralfseger
 *
 */
public class OS {

	private final static String os = System.getProperty("os.name");
	
	public final static boolean isWindows() {
		return os.toLowerCase().indexOf("windows")>-1;
	}
}
