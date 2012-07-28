package org.rosuda.util.r.impl;

import java.io.File;
import java.util.Comparator;
import java.util.Date;

public class FileDateComparator implements Comparator<File> {

    @Override
    public int compare(File f1, File f2) {
	if (f1 == null || !f1.canRead()) {
	    return 1;
	} else if (f2 == null || !f2.canRead()) {
	    return -1;
	} else {
	    final Date fileAge1 = new Date(f1.lastModified());
	    final Date fileAge2 = new Date(f2.lastModified());
	    if (fileAge1.compareTo(fileAge2) == 0) {
		return f1.getName().compareTo(f2.getName());
	    } else {
		return -fileAge1.compareTo(fileAge2);
	    }
	}
    }

}
