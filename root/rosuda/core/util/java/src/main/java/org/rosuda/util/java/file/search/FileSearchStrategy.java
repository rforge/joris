package org.rosuda.util.java.file.search;

import java.io.File;
import java.util.List;

import org.rosuda.util.java.file.FilenameMatcher;

public interface FileSearchStrategy {
    void searchFor(final FilenameMatcher matcher, final List<File> matchingFiles);
}
