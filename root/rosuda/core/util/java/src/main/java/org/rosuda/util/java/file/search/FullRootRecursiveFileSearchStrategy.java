package org.rosuda.util.java.file.search;

import java.io.File;
import java.util.List;

import org.rosuda.util.java.file.FileSystem;
import org.rosuda.util.java.file.FileMatcher;

public class FullRootRecursiveFileSearchStrategy extends AbstractFileSearchStrategy {

    public FullRootRecursiveFileSearchStrategy(FileSystem fileSystem) {
        super(fileSystem);
    }

    @Override
    protected void handleSearchFor(FileMatcher matcher, List<File> matchingFiles) {
        LOGGER.debug("\"" + matcher.describe() + "\" not found in home or paths, scanning from root.");
        scanFoldersUntilMatched(fileSystem.listRoots(), matcher, matchingFiles);
    }

}
