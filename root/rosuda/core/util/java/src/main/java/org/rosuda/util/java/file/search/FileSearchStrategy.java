package org.rosuda.util.java.file.search;

import java.io.File;
import java.util.List;

import org.rosuda.util.java.file.FileMatcher;

public interface FileSearchStrategy {
    void searchFor(final FileMatcher matcher, final List<File> matchingFiles);
}
