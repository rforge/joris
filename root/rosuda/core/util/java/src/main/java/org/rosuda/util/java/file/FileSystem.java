package org.rosuda.util.java.file;

import java.io.File;

public interface FileSystem {

    File[] listRoots();

    File[] listFilesFromHomeDirectory();

    File getChild(File parent, String fileName);

    Iterable<File> pathelements();

}
