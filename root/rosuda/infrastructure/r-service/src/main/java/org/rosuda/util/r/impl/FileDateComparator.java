package org.rosuda.util.r.impl;

import java.io.File;
import java.util.Comparator;

public class FileDateComparator implements Comparator<File> {

	@Override
	public int compare(File f1, File f2) {
		if (f1 == null || !f1.canRead()) {
			return 1;
		} else if (f2 == null || !f2.canRead()) {
			return -1;
		} else if (f1.lastModified() > f2.lastModified()) {
			return -1;
		} else {
			return 1;
		}
	}

}
